package com.priyajit.ecommerce.product.catalog.service.validator;

import com.priyajit.ecommerce.product.catalog.service.exception.CustomConstraintViolationException;
import jakarta.validation.*;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ConstraintViolationValidator<T> {

    void validate(T object) throws ConstraintViolationException {

        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(object);
        for (var violation : violations) {
            System.out.println(violation);
            String message = violation.getPropertyPath() + " " + violation.getMessage();
            throw new CustomConstraintViolationException(message);
        }
    }
}