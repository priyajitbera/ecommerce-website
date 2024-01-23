package com.priyajit.ecommerce.user.management.dto.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * DTO class to contain an email message to send
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailDto implements Serializable {

    private List<String> to;
    private List<String> cc;
    private List<String> bcc;
    private String subject;
    private String body;
    private Map<String, String> correlationIds;

    public static SendEmailDto build(
            List<String> to,
            List<String> cc,
            List<String> bcc,
            String subject,
            String body,
            Map<String, String> correlationIds
    ) {
        return SendEmailDto.builder()
                .to(to)
                .cc(cc)
                .bcc(bcc)
                .subject(subject)
                .body(body)
                .correlationIds(correlationIds)
                .build();
    }
}
