package com.priyajit.email.service.controller;

import com.priyajit.email.service.dto.SendEmailDto;
import com.priyajit.email.service.model.SendEmailModel;
import com.priyajit.email.service.service.EmailSenderService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/email-sender")
public class EmailSenderController {

    private EmailSenderService emailSenderService;

    public EmailSenderController(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @PostMapping("/send-emails")
    List<SendEmailModel> sendEmails(
            @RequestBody List<SendEmailDto> dtoList
    ) {
        return emailSenderService.sendEmail(dtoList);
    }

}
