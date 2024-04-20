package com.priyajit.order.management.service.mongodoc;

import com.priyajit.order.management.service.domain.DeliveryStatus;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryInfo {

    private DeliveryStatus deliveryStatus;
    private List<DeliveryUpdate> deliveryUpdates;
    private DeliveryAddress deliveryAddress;
}
