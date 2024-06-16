package com.priyajit.ecommerce.user.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseModel {

    private String timestamp;
    private String path;
    private String status;
    private String error;
    private String requestId;
}
