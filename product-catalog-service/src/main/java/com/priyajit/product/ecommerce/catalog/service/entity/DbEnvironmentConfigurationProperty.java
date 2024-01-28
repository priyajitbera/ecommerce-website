package com.priyajit.product.ecommerce.catalog.service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.ZonedDateTime;
import java.util.Objects;

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
    @JoinColumn(foreignKey = @ForeignKey(name = "FK__DB_ENV_CONF_PROP__DB_ENV_CONF_ID__01"))
    private DbEnvironmentConfiguration configuration;

    // key should be unique to each DbEnvironmentConfiguration
    @Column(name = "KEY_NAME") // 'key' is a reserved keyword in mysql
    private String key;

    private String value;

    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DbEnvironmentConfigurationProperty that = (DbEnvironmentConfigurationProperty) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
