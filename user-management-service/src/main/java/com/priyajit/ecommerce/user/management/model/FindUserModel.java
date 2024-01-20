package com.priyajit.ecommerce.user.management.model;

import com.priyajit.ecommerce.user.management.entity.User;
import lombok.Builder;
import lombok.Data;

import java.math.BigInteger;

@Data
@Builder
public class FindUserModel {

    private BigInteger id;
    private String email;
    private String name;

    public static FindUserModel buildFrom(User user) {
        if (user == null) return null;

        return FindUserModel.builder()
                .id(user.getId())
                .email(user.getEmailId())
                .name(user.getName())
                .build();
    }
}
