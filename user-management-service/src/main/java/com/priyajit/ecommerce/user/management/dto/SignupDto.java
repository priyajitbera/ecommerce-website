package com.priyajit.ecommerce.user.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    private String emailId;
    private String name;
    private String password;
    private Set<String> roles;
}
