package com.priyajit.ecommerce.user.management.entity;

import jakarta.persistence.*;
import jakarta.validation.Constraint;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
