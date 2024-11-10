package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class DuplicateInvitationException extends CustomException {

    public DuplicateInvitationException() {super(ErrorCode.INVITATION_DUPLICATE);}
}
