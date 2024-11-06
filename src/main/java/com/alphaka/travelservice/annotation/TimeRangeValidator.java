package com.alphaka.travelservice.annotation;

import com.alphaka.travelservice.dto.request.TravelScheduleRequest;
import com.alphaka.travelservice.entity.TravelPlans;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TimeRangeValidator implements ConstraintValidator<ValidTimeRange, TravelScheduleRequest> {

    // 시작 시간이 종료 시간보다 빠르지 않으면 유효하지 않은 값으로 판단
    @Override
    public boolean isValid(TravelScheduleRequest scheduleRequest, ConstraintValidatorContext context) {
        if (scheduleRequest.getStartTime() == null || scheduleRequest.getEndTime() == null) {
            return true;
        }
        return !scheduleRequest.getEndTime().isBefore(scheduleRequest.getStartTime());
    }
}