package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class InvalidTravelScheduleException extends CustomException {
    public InvalidTravelScheduleException() {
        super(ErrorCode.INVALID_TRAVEL_SCHEDULE);
    }
}
