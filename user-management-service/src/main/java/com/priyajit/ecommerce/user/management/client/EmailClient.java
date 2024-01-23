package com.priyajit.ecommerce.user.management.client;

import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;

public interface EmailClient {

    EmailClientStatus send(
            SendEmailDto dto
    );
}
