package com.priyajit.email.service.service.impl;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.model.*;
import com.priyajit.email.service.dto.SendEmailDto;
import com.priyajit.email.service.enitity.enums.EmailMessageBodyType;
import com.priyajit.email.service.enitity.enums.EmailSendStatus;
import com.priyajit.email.service.model.SendEmailModel;
import com.priyajit.email.service.service.EmailSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Primary
public class EmailSenderServiceImplV1 implements EmailSenderService {

    @Value("${aws.ses.region}")
    private Regions AWS_SES_REGION;

    @Value("${aws.ses.sender}")
    private String AWS_SES_SENDER;

    @Autowired
    private AmazonSimpleEmailService amazonSimpleEmailService;

    @Override
    public List<SendEmailModel> sendEmail(List<SendEmailDto> dtoList) {

        return dtoList.stream()
                .map(this::sendEmail)
                .collect(Collectors.toList());
    }

    private SendEmailModel sendEmail(SendEmailDto dto) {

        SendEmailRequest request = createSendEmailRequest(
                AWS_SES_SENDER,
                dto.getTo(),
                dto.getCc(),
                dto.getBcc(),
                dto.getSubject(),
                dto.getBody(),
                dto.getBodyType()
        );

        try {
            SendEmailResult sendEmailResult = amazonSimpleEmailService.sendEmail(request);
            log.info("Successfully sent email messageId:{}", sendEmailResult.getMessageId());

            return SendEmailModel.builder()
                    .correlationIds(dto.getCorrelationIds())
                    .status(EmailSendStatus.SUCCESS)
                    .messageId(sendEmailResult.getMessageId())
                    .correlationIds(dto.getCorrelationIds())
                    .build();
        } catch (Exception e) {
            log.error("Failed to send email {}", e.getMessage());
            e.printStackTrace();

            return SendEmailModel.builder()
                    .correlationIds(dto.getCorrelationIds())
                    .status(EmailSendStatus.FAILURE)
                    .failureReason(e.getMessage())
                    .correlationIds(dto.getCorrelationIds())
                    .build();
        }
    }

    /**
     * Helper method to create SendEmailRequest
     *
     * @param from
     * @param toList
     * @param ccList
     * @param bccList
     * @param subject
     * @param body
     * @param bodyType
     * @return
     */
    private SendEmailRequest createSendEmailRequest(
            String from,
            List<String> toList,
            List<String> ccList,
            List<String> bccList,
            String subject,
            String body,
            EmailMessageBodyType bodyType
    ) {
        Destination destination = new Destination()
                .withToAddresses(toList)
                .withCcAddresses(ccList)
                .withBccAddresses(bccList);

        Body messageBody = createMessageBody(body, bodyType);

        SendEmailRequest request = new SendEmailRequest()
                .withSource(from)
                .withDestination(destination)
                .withMessage(new Message()
                        .withBody(messageBody)
                        .withSubject(new Content()
                                .withCharset("UTF-8").withData(subject)));

        return request;
    }

    /**
     * Helper method to create email message body
     *
     * @param bodyContent the content of email message body
     * @param bodyType    the type of content TEXT, HTML
     * @return
     */
    private Body createMessageBody(String bodyContent, EmailMessageBodyType bodyType) {
        Body body = new Body();
        if (EmailMessageBodyType.HTML == bodyType) {
            Content content = new Content().withData(bodyContent);
            body.withHtml(content);
        }
        // default EmailMessageBodyType.TEXT
        else {
            Content content = new Content().withData(bodyContent);
            body.withText(content);
        }
        return body;
    }
}
