package com.alphaka.travelservice.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TimeRangeValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTimeRange {
    String message() default "종료 시간은 시작 시간보다 빠를 수 없습니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
