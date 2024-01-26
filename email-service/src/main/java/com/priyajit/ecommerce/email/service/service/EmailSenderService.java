package com.priyajit.ecommerce.email.service.service;

import com.priyajit.ecommerce.email.service.dto.SendEmailDto;
import com.priyajit.ecommerce.email.service.model.SendEmailModel;

import java.util.List;

public interface EmailSenderService {

    List<SendEmailModel> sendEmail(List<SendEmailDto> dtoList);
}
