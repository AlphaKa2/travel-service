package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super(ErrorCode.UNAUTHORIZED);
    }
}