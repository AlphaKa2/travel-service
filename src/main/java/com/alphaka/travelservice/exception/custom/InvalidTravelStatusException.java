package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class InvalidTravelStatusException extends CustomException {
    public InvalidTravelStatusException() {
        super(ErrorCode.INVALID_TRAVEL_STATUS);
    }
}
