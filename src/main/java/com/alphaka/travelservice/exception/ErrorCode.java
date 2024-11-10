package com.alphaka.travelservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Auth
    UNAUTHORIZED(401, "AUT001", "인증되지 않은 사용자입니다."),

    // User
    USER_NOT_FOUND(404, "USR001", "존재하지 않는 사용자입니다."),

    // Travel
    PLAN_NOT_FOUND(404, "TRV001", "존재하지 않는 여행입니다."),
    INVALID_TRAVEL_DAY(400, "TRV002", "여행 일자가 올바르지 않습니다."),
    INVALID_TRAVEL_SCHEDULE(400, "TRV003", "여행 스케줄이 올바르지 않습니다."),
    INVALID_TRAVEL_STATUS(400, "TRV004", "여행 상태가 올바르지 않습니다."),
    REVIEW_ALREADY_WRITTEN(400, "TRV005", "이미 리뷰를 작성한 여행입니다."),
    PARTICIPANT_NOT_FOUND(404, "TRV006", "존재하지 않는 참여 정보 입니다."),
    INVITATION_NOT_FOUND(404, "TRV007", "존재하지 않는 초대자 정보 입니다."),
    PARTICIPANT_NOT_ACCESS(401, "TRV008", "해당 여행지에 대한 권한이 없습니다."),
    INVITATION_NOT_ACCESS(401, "TRV009", "해당 여행지에 대한 초대 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}