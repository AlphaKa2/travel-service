package com.alphaka.travelservice.annotation;

import com.alphaka.travelservice.dto.request.TravelScheduleRequest;
import com.alphaka.travelservice.dto.request.TravelScheduleUpdateRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, Object> {

    // 시작 시간이 종료 시간보다 빠르지 않으면 유효하지 않은 값으로 판단
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof TravelScheduleRequest request) {
            return isTimeRangeValid(request.getStartTime(), request.getEndTime());
        } else if (value instanceof TravelScheduleUpdateRequest request) {
            return isTimeRangeValid(request.getStartTime(), request.getEndTime());
        }
        // 다른 타입은 유효하지 않다고 간주
        return false;
    }

    private boolean isTimeRangeValid(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            return false; // null 값은 다른 검증에서 처리될 수 있음
        }
        return !startTime.isAfter(endTime);
    }
}