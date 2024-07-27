package com.priyajit.ecommerce.facade.service.config.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("user-token-parser")
@AllArgsConstructor
@Getter
public class UserTokenParserProperties {

    private String algorithm;
    private String secret;
}
