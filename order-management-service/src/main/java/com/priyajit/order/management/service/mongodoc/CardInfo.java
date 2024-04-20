package com.priyajit.order.management.service.mongodoc;

import com.priyajit.order.management.service.domain.CardType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CardInfo {

    private CardType cardType;
    private String cardNumber;
    private String bankName;
    private String cardHolderName;
}
