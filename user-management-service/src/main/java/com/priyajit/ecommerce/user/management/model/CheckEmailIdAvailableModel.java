package com.priyajit.ecommerce.user.management.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckEmailIdAvailableModel {

    private String emailId;
    private Boolean available;
}
