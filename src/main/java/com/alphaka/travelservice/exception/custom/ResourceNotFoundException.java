package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException() {
        super(ErrorCode.INVITATION_NOT_FOUND);
    }
}
