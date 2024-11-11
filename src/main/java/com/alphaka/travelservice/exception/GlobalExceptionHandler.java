package com.alphaka.travelservice.exception;

import com.alphaka.travelservice.common.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 모든 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("CustomException occurred: {}", e.getErrorCode().getMessage());
        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorCode().getStatus(),
                e.getErrorCode().getCode(),
                e.getErrorCode().getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(e.getErrorCode().getStatus()));
    }
}