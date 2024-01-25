package com.priyajit.ecommerce.user.management.component;

import com.priyajit.ecommerce.user.management.dto.external.SendEmailDto;
import com.priyajit.ecommerce.user.management.entity.enums.EmailClientStatus;

public interface EmailSender {

    EmailClientStatus send(
            SendEmailDto dto
    );
}
