package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class InvitationAccessException extends CustomException {

    public InvitationAccessException() { super(ErrorCode.INVITATION_NOT_ACCESS); }
}
