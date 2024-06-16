package com.priyajit.ecommerce.cart.service.mogodoc;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.beans.Transient;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document
public class DbEnvironmentConfiguration {

    @Id
    private String id;

    @CreatedDate
    private ZonedDateTime createdOn;

    @LastModifiedDate
    private ZonedDateTime lastModifiedOn;

    private Boolean isActive;

    private List<Property> properties;

    private String version;

    @Transient
    public String getProperty(String key) {

        // filter by the given key
        List<Property> filtered = getProperties().stream()
                .filter(prop -> Objects.equals(key, prop.getKey()))
                .collect(Collectors.toList());

        // if not property found for given key
        if (filtered.size() == 0) return null;

        else return filtered.get(0).getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbEnvironmentConfiguration that = (DbEnvironmentConfiguration) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public static class Keys {

        // product-catalog-service
        public final static String PRODUCT_CATALOG_SERVICE_BASE_URL = "product-catalog-service.base-url";

        // Redis
        public final static String REDIS_HOSTNAME = "redis.hostname";
        public final static String REDIS_PORT = "redis.port";

        // freecurrencyapi.com service
        public final static String FREECURRENCY_SERVICE_BASE_URL = "freecurrency-service.base-url";
        public final static String FREECURRENCY_SERVICE_API_KEY = "freecurrency-service.api-key";

        // JWT
        public final static String JWT_SECRET_KEY = "jwt.secret-key";
    }

    public static class Values {
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Document
    public static class Property {

        @Id
        private Long id;

        private ZonedDateTime createdOn;

        private ZonedDateTime lastModifiedOn;

        private DbEnvironmentConfiguration configuration;

        private String key;

        private String value;

        private String description;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Property that = (Property) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
