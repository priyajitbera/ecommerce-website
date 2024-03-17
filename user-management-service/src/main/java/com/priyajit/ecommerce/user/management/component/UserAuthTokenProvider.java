package com.priyajit.ecommerce.user.management.component;

public interface UserAuthTokenProvider {
    String generateToken(String sub);
}
