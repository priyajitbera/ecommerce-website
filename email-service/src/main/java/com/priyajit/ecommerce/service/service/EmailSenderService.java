package com.priyajit.ecommerce.service.service;

import com.priyajit.ecommerce.service.dto.SendEmailDto;
import com.priyajit.ecommerce.service.model.SendEmailModel;

import java.util.List;

public interface EmailSenderService {

    List<SendEmailModel> sendEmail(List<SendEmailDto> dtoList);
}
