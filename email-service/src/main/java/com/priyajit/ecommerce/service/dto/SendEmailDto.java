package com.priyajit.ecommerce.service.dto;

import com.priyajit.ecommerce.service.enitity.enums.EmailMessageBodyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDto {

    private List<String> toList;
    private List<String> ccList;
    private List<String> bccList;
    private String subject;
    private String body;
    private EmailMessageBodyType bodyType;
    private Map<String, String> correlationIds;
}
