package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class InvalidTravelDayException extends CustomException {
    public InvalidTravelDayException() {
        super(ErrorCode.INVALID_TRAVEL_DAY);
    }
}
