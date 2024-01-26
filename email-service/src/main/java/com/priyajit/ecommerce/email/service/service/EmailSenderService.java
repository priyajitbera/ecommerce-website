package com.priyajit.email.service.service;

import com.priyajit.email.service.dto.SendEmailDto;
import com.priyajit.email.service.model.SendEmailModel;

import java.util.List;

public interface EmailSenderService {

    List<SendEmailModel> sendEmail(List<SendEmailDto> dtoList);
}
