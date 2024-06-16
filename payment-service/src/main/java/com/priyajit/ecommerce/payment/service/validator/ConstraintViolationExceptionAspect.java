package com.priyajit.ecommerce.payment.service.validator;

import jakarta.validation.Valid;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Aspect
@Component
public class ConstraintViolationExceptionAspect {

    private ConstraintViolationValidator constraintViolationValidator;

    public ConstraintViolationExceptionAspect(ConstraintViolationValidator constraintViolationValidator) {
        this.constraintViolationValidator = constraintViolationValidator;
    }

    /**
     * Looks for arguments annotated with jakarta.validation.Valid and validates with ConstraintViolationValidator
     *
     * @param jp the JointPoint
     * @throws NoSuchMethodException
     */
    @Before("execution(* com.priyajit.ecommerce.cart.service.service.*.*.*(..))")
    void validate(JoinPoint jp) {

        MethodSignature methodSignature = (MethodSignature) jp.getSignature();
        Method method = methodSignature.getMethod();
        Parameter[] parameters = method.getParameters();

        for (int i = 0; i < parameters.length; i++) {
            for (var annotation : parameters[i].getAnnotations()) {
                // check if argument is annotated with jakarta.validation.Valid
                if (annotation.annotationType() == Valid.class) {
                    var arg = jp.getArgs()[i];
                    constraintViolationValidator.validate(arg);
                }
            }
        }
    }
}
