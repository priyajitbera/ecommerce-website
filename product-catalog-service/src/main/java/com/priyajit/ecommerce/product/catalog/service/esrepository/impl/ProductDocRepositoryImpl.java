package com.priyajit.ecommerce.product.catalog.service.esrepository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import com.priyajit.ecommerce.product.catalog.service.esrepository.ProductDocRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration.Keys;

@Slf4j
@Component
public class ProductDocRepositoryImpl implements ProductDocRepository {

    private ElasticsearchClient elasticsearchClient;
    private DbEnvironmentConfiguration dbEnvironmentConfiguration;

    public ProductDocRepositoryImpl(ElasticsearchClient elasticsearchClient, DbEnvironmentConfiguration dbEnvironmentConfiguration) {
        this.elasticsearchClient = elasticsearchClient;
        this.dbEnvironmentConfiguration = dbEnvironmentConfiguration;
    }

    /**
     * @param productDocs
     * @return
     */
    @Override
    public List<String> indexAll(List<ProductDoc> productDocs) {
        if (productDocs == null)
            throw new IllegalArgumentException("Unexpected null value provided for productDocs:List<ProductDoc>");

        var bulkRequestBuilder = new BulkRequest.Builder();
        // for each product create the indexing operations
        String INDEX_NAME = dbEnvironmentConfiguration.getProperty(Keys.ELASTIC_SEARCH_PRODUCT_INDEX);
        productDocs.forEach(product -> bulkRequestBuilder.operations(ops -> ops
                .index(idx -> idx
                        .index(INDEX_NAME)
                        .id(product.getId())
                        .document(product)
                )
        ));
        BulkResponse bulkResponse = null;
        try {
            log.info("Before executing bulk index");
            bulkResponse = elasticsearchClient.bulk(bulkRequestBuilder.build());
            log.info("After executing bulk index, errors:{}", bulkResponse.errors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (bulkResponse.errors()) {
            String errorMessage = buildErrorMessage(bulkResponse.items());
            throw new RuntimeException("Error while performing bulk index operation. " + errorMessage);
        } else {
            return bulkResponse.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());
        }
    }

    /**
     * Method to search indexed ProductDoc items
     *
     * @param productIds
     * @param productNamePart
     * @param productDescriptionPart
     * @param productCategoryIds
     * @param productCategoryNames
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public Page<ProductDoc> search(
            @Nullable List<String> productIds,
            @Nullable String productNamePart,
            @Nullable String productDescriptionPart,
            @Nullable List<String> productCategoryIds,
            @Nullable List<String> productCategoryNames,
            int pageIndex, int pageSize
    ) {

        try {
            Query.Builder queryBuilder = new Query.Builder();

            // filter based on productIds
            if (productIds != null) {
                List<Query> queries = productIds.stream()
                        .map(id -> new Query.Builder().match(t -> t.field("id").query(id)).build())
                        .collect(Collectors.toList());
                queryBuilder.bool(b -> b.should(queries));
            }

            // filter based on productNamePart
            if (productNamePart != null) {
                queryBuilder.match(q -> q.field("title").query(productNamePart));
            }

            // filter based on productDescriptionPart
            if (productDescriptionPart != null) {
                queryBuilder.match(q -> q.field("description").query(productDescriptionPart));
            }

            // filter based on productCategoryIds
            if (productCategoryIds != null) {
                List<Query> queries = productCategoryIds.stream()
                        .map(id -> new Query.Builder().match(t -> t.field("taggedCategories.id").query(id)).build())
                        .collect(Collectors.toList());
                queryBuilder.bool(b -> b.should(queries));
            }

            // filter based on productCategoryNames
            if (productCategoryNames != null) {
                List<Query> queries = productCategoryNames.stream()
                        .map(name -> new Query.Builder().match(t -> t.field("taggedCategories.name").query(name)).build())
                        .collect(Collectors.toList());
                queryBuilder.bool(b -> b.should(queries));
            }

            // build query
            Query query = queryBuilder.build();

            log.info("Elastic Search Query: {}", query.toString());

            // created count & result query
            var totalFuture = CompletableFuture.supplyAsync(elasticsearchClient.count(r -> r.query(query))::count);
            var resultFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            String INDEX_NAME = dbEnvironmentConfiguration.getProperty(Keys.ELASTIC_SEARCH_PRODUCT_INDEX);
                            return elasticsearchClient.search(s -> s
                                            .index(INDEX_NAME)
                                            .size(pageSize)
                                            .from(pageIndex * pageSize)
                                            .query(query),
                                    ProductDoc.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
            );

            // fetch count & result, return Page of ProductDoc
            return CompletableFuture.allOf(totalFuture, resultFuture).thenApply(v -> {

                var response = resultFuture.join();
                log.info("hits count: " + response.hits().hits().size());

                List<ProductDoc> productDocs = response.hits().hits().stream()
                        .map(Hit::source)
                        .collect(Collectors.toList());

                return new PageImpl<>(productDocs, Pageable.ofSize(pageSize), totalFuture.join());
            }).join();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to deleted indexed ProductDoc
     *
     * @param productDocIds
     * @return
     */
    @Override
    public List<String> deleteAll(List<String> productDocIds) {
        if (productDocIds == null)
            throw new IllegalArgumentException("Unexpected null value provided for productDocIds:List<String>");

        BulkRequest.Builder requestBuilder = new BulkRequest.Builder();

        String INDEX_NAME = dbEnvironmentConfiguration.getProperty(Keys.ELASTIC_SEARCH_PRODUCT_INDEX);
        List<BulkOperation> bulkOperations = productDocIds.stream()
                .map(id -> new BulkOperation.Builder().delete(d -> d.id(id).index(INDEX_NAME)).build())
                .collect(Collectors.toList());

        requestBuilder.operations(bulkOperations);
        BulkResponse bulkResponse;
        try {
            log.info("Before executing bulk index");
            bulkResponse = elasticsearchClient.bulk(requestBuilder.build());
            log.info("After executing bulk index, errors:{}", bulkResponse.errors());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // if error present response, accumulate the error cause and through exception
        if (bulkResponse.errors()) {
            throw new RuntimeException("Error while performing bulk delete operation " + buildErrorMessage(bulkResponse.items()));
        } else {
            return bulkResponse.items().stream().map(BulkResponseItem::id).collect(Collectors.toList());
        }
    }


    /**
     * Helper method to accumulate error message form a list of BulkResponseItem
     *
     * @param items
     * @return
     */
    private String buildErrorMessage(List<BulkResponseItem> items) {
        if (items == null)
            throw new IllegalArgumentException("Unexpected null values provided for items:List<BulkResponseItem>");

        StringBuilder errorMessage = new StringBuilder();
        items.forEach(item -> errorMessage.append(item.error().toString()));

        return errorMessage.toString();
    }

}

