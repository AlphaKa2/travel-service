package com.alphaka.travelservice.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Auth
    UNAUTHORIZED(401, "AUT001", "인증되지 않은 사용자입니다."),
    SIGNIN_REQUIRED(401, "AUT002", "로그인이 필요합니다."),

    // User
    USER_NOT_FOUND(404, "USR001", "존재하지 않는 사용자입니다."),

    // Blog
    BLOG_CREATION_FAILED(500, "BLG001", "블로그 생성 중 오류가 발생했습니다."),
    BLOG_NOT_FOUND(404, "BLG002", "존재하지 않는 블로그입니다."),

    // Post
    POST_NOT_FOUND(404, "PST001", "존재하지 않는 포스트입니다."),

    // Comment
    COMMENT_NOT_FOUND(404, "CMT001", "존재하지 않는 댓글입니다."),
    PARENT_COMMENT_NOT_FOUND(404, "CMT002", "존재하지 않는 부모 댓글입니다."),

    // S3
    S3_FILE_EMPTY(400, "S3_001", "파일이 비어있습니다."),
    S3_FILE_EXTENSION_INVALID(400, "S3_002", "지원하지 않는 파일 확장자입니다."),
    S3_FILE_EXTENSION_MISSING(400, "S3_003", "파일 확장자가 없습니다."),
    S3_FILE_UPLOAD_FAILED(500, "S3_004", "파일 업로드 중 오류가 발생했습니다."),
    S3_OBJECT_UPLOAD_FAILED(500, "S3_005", "객체 업로드 중 오류가 발생했습니다."),
    S3_FILE_SIZE_EXCEEDED(400, "S3_006", "파일 크기가 너무 큽니다."),

    // Travel
    PLAN_NOT_FOUND(404, "PLN001", "존재하지 않는 여행입니다."),
    INVALID_TRAVEL_DAY(400, "PLN002", "여행 일자가 올바르지 않습니다."),

    // Participant
    PARTICIPANT_NOT_FOUND(404, "PAL001", "존재하지 않는 참여 정보 입니다."),

    // Invitation
    INVITATION_NOT_FOUND(404, "INV001", "존재하지 않는 초대자 정보 입니다.");

    private final int status;
    private final String code;
    private final String message;
}