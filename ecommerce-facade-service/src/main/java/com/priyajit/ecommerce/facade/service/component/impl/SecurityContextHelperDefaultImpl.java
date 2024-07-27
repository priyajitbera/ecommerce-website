package com.priyajit.ecommerce.facade.service.component.impl;

import com.priyajit.ecommerce.facade.service.component.SecurityContextHelper;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SecurityContextHelperDefaultImpl implements SecurityContextHelper {

    /***
     * Parses userId from security context
     * @return
     */
    @Override
    public Mono<String> getUserId() {
        return ReactiveSecurityContextHolder.getContext().map(SecurityContext::getAuthentication)
                .map(auth -> {
                    if (Jwt.class == auth.getPrincipal().getClass()) {
                        var jwt = (Jwt) auth.getPrincipal();
                        return (String) jwt.getClaims().get("sub");
                    } else {
                        throw new RuntimeException("Unknown auth implementation");
                    }
                });
    }
}
