package com.priyajit.ecommerce.user.management.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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


    public static class Keys {

        // Bcrypt
        public final static String BCRYPT_VERSION = "bcrypt-version";
        public final static String BCRYPT_STRENGTH = "bcrypt-strength";

        // strategies
        public static final String EMAIL_SENDING_STRATEGY = "email-sending-strategy";

        // Kafka
        public final static String KAFKA_PRODUCER_CONFIG_ENABLE = "kafka.producer.config.enable";
        public final static String KAFKA_BOOTSTRAP_ADDRESS = "kafka.bootstrap-address";
        public final static String KAFKA_TOPIC = "kafka.topic";
        public final static String KAFKA_MAX_REQUEST_SIZE = "kafka.max-request-size";

        // AWS SQS
        public final static String AWS_SQS_CONFIG_ENABLE = "aws.sqs.config.enable";
        public final static String AWS_SQS_REGION = "aws.sqs.region";
        public final static String AWS_SQS_QUEUE_URL = "aws.sqs.queue-url";
        public final static String AWS_SQS_ACCESS_KEY = "aws.sqs.access-key";
        public final static String AWS_SQS_SECRET_KEY = "aws.sqs.secret-key";
    }

    public static class Values {
        public final static String EMAIL_SENDING_STRATEGY_AWS_SQS = "AWS_SQS";
        public final static String EMAIL_SENDING_STRATEGY_KAFKA = "KAFKA";
    }
}
