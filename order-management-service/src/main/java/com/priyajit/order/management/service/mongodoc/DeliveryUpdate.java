package com.priyajit.order.management.service.mongodoc;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryUpdate {

    private LocalDateTime timeStamp;
    private String message;
}
