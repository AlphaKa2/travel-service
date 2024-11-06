package com.alphaka.travelservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDateRange {
    String message() default "종료일은 시작일보다 빠를 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
