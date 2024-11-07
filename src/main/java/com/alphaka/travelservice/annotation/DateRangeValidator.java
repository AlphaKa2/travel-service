package com.alphaka.travelservice.annotation;

import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import com.alphaka.travelservice.dto.request.TravelPlanUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, Object> {

    // 시작일이 종료일보다 빠르면 유효하지 않은 값으로 판단
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof TravelPlanCreateRequest request) {
            return isDateRangeValid(request.getStartDate(), request.getEndDate());
        } else if (value instanceof TravelPlanUpdateRequest request) {
            return isDateRangeValid(request.getStartDate(), request.getEndDate());
        }
        // 다른 타입은 유효하지 않다고 간주
        return false;
    }

    private boolean isDateRangeValid(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return false; // null 값은 다른 검증에서 처리될 수 있음
        }
        return !startDate.isAfter(endDate);
    }
}
