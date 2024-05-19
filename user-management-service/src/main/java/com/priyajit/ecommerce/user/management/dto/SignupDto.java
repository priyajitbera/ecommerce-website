package com.priyajit.ecommerce.user.management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupDto {

    @NotNull
    private Boolean signUpAsSeller;
    @NotBlank
    private String emailId;
    @NotBlank
    private String name;
    @NotBlank
    private String password;
}
