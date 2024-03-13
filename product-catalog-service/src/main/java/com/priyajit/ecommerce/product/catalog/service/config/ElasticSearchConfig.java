package com.priyajit.ecommerce.product.catalog.service.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;

import javax.net.ssl.SSLContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

import static com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration.Keys;
import static com.priyajit.ecommerce.product.catalog.service.entity.DbEnvironmentConfiguration.Values;

@Slf4j
@Configuration
public class ElasticSearchConfig {

    @Bean("defaultElasticSearchClient")
    public ElasticsearchClient elasticsearchClient(DbEnvironmentConfiguration configuration, S3Client s3Client) {

        try {
            KeyStore truststore = KeyStore.getInstance(configuration.getProperty(Keys.ELASTIC_SEARCH_TRUST_STORE_TYPE));

            String TRUST_STORE_SOURCE = configuration.getProperty(Keys.ELASTIC_SEARCH_TRUST_STORE_SOURCE);
            String TRUST_STORE_PASSWORD = configuration.getProperty(Keys.ELASTIC_SEARCH_TRUST_STORE_PASSWORD);

            // load KeyStore from filesystem
            log.info("Elasticsearch Trust Store configured source {}:{}", Keys.ELASTIC_SEARCH_TRUST_STORE_SOURCE, TRUST_STORE_SOURCE);
            if (Values.ELASTIC_SEARCH_TRUST_STORE_SOURCE_FILE_SYSTEM.equals(TRUST_STORE_SOURCE)) {
                Path trustStorePath = Paths.get(configuration.getProperty(Keys.ELASTIC_SEARCH_TRUST_STORE_PATH));
                truststore.load(Files.newInputStream(trustStorePath), TRUST_STORE_PASSWORD.toCharArray());
            }
            // load KeyStore from AWS S3
            else if (Values.ELASTIC_SEARCH_TRUST_STORE_SOURCE_AWS_S3.equals(TRUST_STORE_SOURCE)) {
                var response = s3Client.getObject(r -> r.bucket(configuration.getProperty(Keys.AWS_S3_BUCKET_NAME)).key(configuration.getProperty(Keys.ELASTIC_SEARCH_TRUST_STORE_S3_KEY)));
                truststore.load(response, TRUST_STORE_PASSWORD.toCharArray());

            }
            // not implemented
            else {
                throw new UnsupportedOperationException(String.format("TrustStore source configuration not implemented for configured %s:%s",
                        Keys.ELASTIC_SEARCH_TRUST_STORE_SOURCE, TRUST_STORE_SOURCE));
            }

            SSLContextBuilder sslBuilder = SSLContexts.custom()
                    .loadTrustMaterial(truststore, null);
            final SSLContext sslContext = sslBuilder.build();

            String SERVER_URL = configuration.getProperty(Keys.ELASTIC_SEARCH_URL);
            String API_KEY = configuration.getProperty(Keys.ELASTIC_SEARCH_API_KEY);
            RestClient restClient = RestClient
                    .builder(HttpHost.create(SERVER_URL))
                    .setDefaultHeaders(new Header[]{
                            new BasicHeader("Authorization", String.format("ApiKey %s", API_KEY))
                    })
                    .setHttpClientConfigCallback(httpAsyncClientBuilder -> httpAsyncClientBuilder.setSSLContext(sslContext))
                    .build();

            // Create the transport with a Jackson mapper
            ElasticsearchTransport transport = new RestClientTransport(
                    restClient, new JacksonJsonpMapper());

            // And create the API client
            ElasticsearchClient esClient = new ElasticsearchClient(transport);
            return esClient;
        } catch (Exception e) {
            throw new RuntimeException("Failed to configure ElasticSearchClient", e);
        }
    }
}
