package com.priyajit.ecommerce.user.management.client.impl;

import com.priyajit.ecommerce.user.management.client.EmailClient;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class EmailClientImplV1 implements EmailClient {

    @Override
    public EmailClientStatus send(List<String> toList, List<Object> ccList, String emailBody) {
        // todo : implement
        System.out.println("Email body: "  + emailBody);
        return EmailClientStatus.SUCCESS;
    }
}
