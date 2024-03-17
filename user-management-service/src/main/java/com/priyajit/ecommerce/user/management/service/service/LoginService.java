package com.priyajit.ecommerce.user.management.service.service;

import com.priyajit.ecommerce.user.management.dto.LoginDto;
import com.priyajit.ecommerce.user.management.model.LoginModel;

public interface LoginService {
    LoginModel login(LoginDto dto);
}
