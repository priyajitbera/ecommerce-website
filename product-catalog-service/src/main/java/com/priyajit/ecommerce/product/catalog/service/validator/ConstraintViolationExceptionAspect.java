package com.priyajit.ecommerce.product.catalog.service.validator;

import jakarta.validation.Valid;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
    @Before("execution(* com.priyajit.ecommerce.product.catalog.service.service.*.*.*(..))")
    void validate(JoinPoint jp) throws NoSuchMethodException {

        String methodName = jp.getSignature().getName();
        Class<?>[] classArr = new Class[jp.getArgs().length];
        for (int i = 0; i < jp.getArgs().length; i++) {
            classArr[i] = jp.getArgs()[i].getClass();
        }
        Method method = jp.getTarget().getClass().getMethod(methodName, classArr);
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
