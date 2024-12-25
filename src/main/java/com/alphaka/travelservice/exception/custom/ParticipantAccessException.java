package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class ParticipantAccessException extends CustomException {

    public ParticipantAccessException() { super(ErrorCode.PARTICIPANT_NOT_ACCESS); }
}
