package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class InvitationOverException extends CustomException {

    public InvitationOverException() {
        super(ErrorCode.INVITATION_OVER);
    }
}
