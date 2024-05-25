package com.priyajit.ecommerce.user.management.component;

import com.priyajit.ecommerce.user.management.model.UserDetailsModel;

public interface UserDetailsParser {

    UserDetailsModel getFromToken(String token);
}
