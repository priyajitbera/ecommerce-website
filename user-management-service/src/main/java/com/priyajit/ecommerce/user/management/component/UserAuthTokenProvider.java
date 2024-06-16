package com.priyajit.ecommerce.user.management.component;

import java.util.List;

public interface UserAuthTokenProvider {
    String generateToken(String sub, List<String> roles);
}
