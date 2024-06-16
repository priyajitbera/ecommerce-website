package com.priyajit.ecommerce.cart.service.validator;

import com.priyajit.ecommerce.cart.service.exception.CustomConstraintViolationException;
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
            String message = violation.getPropertyPath() + " " + violation.getMessage();
            throw new CustomConstraintViolationException(message);
        }
    }
}
