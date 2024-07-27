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
import com.priyajit.ecommerce.product.catalog.service.exception.ElasticSearchConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
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
            var msg = String.format("Error occurred while connecting to ElasticSearch, error: %s", e.getMessage());
            log.error(msg);
            throw new ElasticSearchConnectionException(msg);
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
     * @param searchKeyword
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Override
    public Page<ProductDoc> search(String searchKeyword, int pageIndex, int pageSize) {

        try {
            List<Query> queries = new ArrayList<>();
            // apply searchKeyword on title
            queries.add(
                    new Query.Builder().match(q -> q.field("title").query(searchKeyword)).build()
            );

            // apply searchKeyword on description
            queries.add(
                    new Query.Builder().match(q -> q.field("description").query(searchKeyword)).build()
            );

            // apply searchKeyword on taggedCategories.name
            queries.add(
                    new Query.Builder().match(q -> q.field("taggedCategories.name").query(searchKeyword)).build()
            );

            // build fina query
            Query query = new Query.Builder().bool(boolQuery -> boolQuery.should(queries)).build();

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

        } catch (IOException e) {
            var msg = String.format("Error occurred while connecting to ElasticSearch, error: %s", e.getMessage());
            log.error(msg);
            throw new ElasticSearchConnectionException(msg);
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
            var msg = String.format("Error occurred while connecting to ElasticSearch, error: %s", e.getMessage());
            log.error(msg);
            throw new ElasticSearchConnectionException(msg);
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

