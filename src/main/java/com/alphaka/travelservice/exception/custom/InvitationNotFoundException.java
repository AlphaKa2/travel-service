package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class InvitationNotFoundException extends CustomException {
    public InvitationNotFoundException() {
        super(ErrorCode.INVITATION_NOT_FOUND);
    }
}
