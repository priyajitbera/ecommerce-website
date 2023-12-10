package com.priyajit.ecommerce.user.management.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateUserDto {

    private String email;
    private String name;
}
