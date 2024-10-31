package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}