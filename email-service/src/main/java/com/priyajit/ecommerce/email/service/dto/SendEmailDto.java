package com.priyajit.ecommerce.email.service.dto;

import com.priyajit.ecommerce.email.service.enitity.enums.EmailMessageBodyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * DTO class to contain an Email message to deliver
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDto {

    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private EmailMessageBodyType bodyType;
    private Map<String, String> correlationIds;
}
