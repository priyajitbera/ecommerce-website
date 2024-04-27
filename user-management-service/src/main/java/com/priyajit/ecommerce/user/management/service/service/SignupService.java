package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.SignupDto;
import com.priyajit.ecommerce.user.management.model.SignupModel;

public interface SignupService {

    SignupModel signup(SignupDto dto);
}
