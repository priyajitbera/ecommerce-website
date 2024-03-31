package com.priyajit.order.management.service.mongodoc;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DeliveryAddress {

    private String buildingNumber;
    private String houseNumber;
    private String addressLine1;
    private String addressLine2;
    private String landMark;
    private String contactNumber1;
    private String contactNumber2;
    private String city;
    private Integer pincode;
}
