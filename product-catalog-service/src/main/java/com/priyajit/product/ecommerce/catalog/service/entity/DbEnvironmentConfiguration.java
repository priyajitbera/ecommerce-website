package com.priyajit.product.ecommerce.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.beans.Transient;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DbEnvironmentConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    private Boolean isActive;

    @OneToMany(mappedBy = "configuration", fetch = FetchType.EAGER)
    private List<DbEnvironmentConfigurationProperty> properties;

    private String version;

    @Transient
    public String getProperty(String key) {

        // filter by the given key
        List<DbEnvironmentConfigurationProperty> filtered = getProperties().stream()
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

        // Kafka
        public final static String KAFKA_CONSUMER_CONFIG_ENABLE = "kafka.producer.config.enable";
        public final static String KAFKA_BOOTSTRAP_ADDRESS = "kafka.bootstrap-address";
        public final static String KAFKA_TOPIC = "kafka.topic";
        public final static String KAFKA_MAX_REQUEST_SIZE = "kafka.max-request-size";
        public final static String KAFKA_GROUP_ID_CONFIG = "kafka.group-id-config";

        // AWS SQS
        public final static String AWS_SQS_CONFIG_ENABLE = "aws.sqs.config.enable";
        public final static String AWS_SQS_REGION = "aws.sqs.region";
        public final static String AWS_SQS_QUEUE_URL = "aws.sqs.queue-url";
        public final static String AWS_SQS_ACCESS_KEY = "aws.sqs.access-key";
        public final static String AWS_SQS_SECRET_KEY = "aws.sqs.secret-key";

        // AWS SES
        public final static String AWS_SES_REGION = "aws.ses.region";
        public final static String AWS_SES_ACCESS_KEY = "aws.ses.access-key";
        public final static String AWS_SES_SECRET_KEY = "aws.ses.secret-key";
        public final static String AWS_SES_SENDER_KEY = "aws.ses.sender";
    }

    public static class Values {
    }
}
