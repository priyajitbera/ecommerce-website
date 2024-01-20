package com.priyajit.ecommerce.user.management.client;

import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;

import java.util.List;

public interface EmailClient {
    EmailClientStatus send(List<String> toList, List<Object> ccList, String emailBody);
}
