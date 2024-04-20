package com.priyajit.order.management.service.dto;

import com.priyajit.order.management.service.domain.DeliveryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateDeliveryStatusDto {


    private String orderId;
    private DeliveryStatus deliveryStatus;
}
