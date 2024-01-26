package com.priyajit.email.service.model;

import com.priyajit.email.service.enitity.enums.EmailSendStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class SendEmailModel {

    private EmailSendStatus status;
    private String failureReason;
    private Map<String, String> correlationIds;
    private String messageId;
}
