package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class ReviewAlreadyWrittenException extends CustomException {
    public ReviewAlreadyWrittenException() {
        super(ErrorCode.REVIEW_ALREADY_WRITTEN);
    }
}
