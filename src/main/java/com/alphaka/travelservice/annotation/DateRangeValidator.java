package com.alphaka.travelservice.annotation;

import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, TravelPlanCreateRequest> {

    // 시작일이 종료일보다 빠르면 유효하지 않은 값으로 판단
    @Override
    public boolean isValid(TravelPlanCreateRequest request, ConstraintValidatorContext context) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return true;
        }
        return request.getStartDate().isBefore(request.getEndDate());
    }
}
