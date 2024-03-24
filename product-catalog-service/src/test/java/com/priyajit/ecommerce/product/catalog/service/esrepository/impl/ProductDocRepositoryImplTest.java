package com.priyajit.ecommerce.product.catalog.service.esrepository.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.bulk.OperationType;
import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductDoc;
import com.priyajit.ecommerce.product.catalog.service.esdoc.ProductPriceDoc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ProductDocRepositoryImplTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @Mock
    private DbEnvironmentConfiguration configuration;

    @InjectMocks
    private ProductDocRepositoryImpl productDocRepository;

    @Test
    void indexAll_success() {
        // arrange
        var productDocs = List.of(ProductDoc.builder()
                .id("PRODUCT-ID-1")
                .title("Iphone 15")
                .description("Latest Iphone from Apple")
                .price(
                        ProductPriceDoc.builder()
                                .id("PRODUCT-PRICE-1")
                                .currency("INR")
                                .price(BigDecimal.valueOf(7000000L))
                                .build()
                )
                .build());
        String INDEX_NAME = "PRODUCT-INDEX";

        // mock
        BDDMockito.when(configuration.getProperty(DbEnvironmentConfiguration.Keys.ELASTIC_SEARCH_PRODUCT_INDEX))
                .thenReturn(INDEX_NAME);
        try {
            BDDMockito.when(elasticsearchClient.bulk(BDDMockito.any(BulkRequest.class)))
                    .then(i -> {
                        BulkRequest bulkRequest = i.getArgument(0, BulkRequest.class);
                        return new BulkResponse.Builder().items(
                                        bulkRequest.operations().stream().map(operation -> new BulkResponseItem.Builder()
                                                .id(operation.index().id())
                                                .index(operation.index().index())
                                                .operationType(operation.isIndex() ? OperationType.Index : null)
                                                .status(200)
                                                .build()
                                        ).collect(Collectors.toList()))
                                .errors(false)
                                .took(1)
                                .build();
                    });

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // act
        List<String> indexedProductDocIds = productDocRepository.indexAll(productDocs);

        // assert
        Assertions.assertEquals(productDocs.size(), indexedProductDocIds.size());
    }
}