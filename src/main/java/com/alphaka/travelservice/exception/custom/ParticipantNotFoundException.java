package com.alphaka.travelservice.exception.custom;

import com.alphaka.travelservice.exception.CustomException;
import com.alphaka.travelservice.exception.ErrorCode;

public class ParticipantNotFoundException extends CustomException {
    public ParticipantNotFoundException() {
        super(ErrorCode.PARTICIPANT_NOT_FOUND);
    }
}
