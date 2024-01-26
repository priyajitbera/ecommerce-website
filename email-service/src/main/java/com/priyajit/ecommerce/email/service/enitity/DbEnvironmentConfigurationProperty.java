package com.priyajit.ecommerce.email.service.enitity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        // key should be unique to each DbEnvironmentConfiguration
        uniqueConstraints = @UniqueConstraint(name = "UNIQ_CONFIG_KEY", columnNames = {"KEY_NAME", "CONFIGURATION_ID"})
)
public class DbEnvironmentConfigurationProperty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    @ManyToOne
    private DbEnvironmentConfiguration configuration;

    // key should be unique to each DbEnvironmentConfiguration
    @Column(name = "KEY_NAME") // 'key' is a reserved keyword in mysql
    private String key;

    private String value;

    private String description;
}
