package com.priyajit.order.management.service.mongodoc;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpiInfo {
    private String upiId;
}
