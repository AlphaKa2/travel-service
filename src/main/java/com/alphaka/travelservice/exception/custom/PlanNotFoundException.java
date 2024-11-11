package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class PlanNotFoundException extends CustomException {

    public PlanNotFoundException() {
        super(ErrorCode.PLAN_NOT_FOUND);
    }
}
