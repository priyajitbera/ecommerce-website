package com.priyajit.ecommerce.payment.service.entity;

import com.priyajit.ecommerce.payment.service.domain.PaymentMode;
import com.priyajit.ecommerce.payment.service.domain.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @CreationTimestamp
    private ZonedDateTime createdOn;

    @UpdateTimestamp
    private ZonedDateTime lastModifiedOn;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;

    private BigDecimal amount;
    private String currency;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.PERSIST)
    private CardInfo cardInfo;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.PERSIST)
    private UpiInfo upiInfo;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "varchar")
    private PaymentStatus paymentStatus;

    private String failureReason;

    private Boolean paymentStatusConfirmationEventPublished;
    private ZonedDateTime paymentStatusConfirmationEventPublishedOn;
}
