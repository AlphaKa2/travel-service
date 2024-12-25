package com.alphaka.travelservice.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;
    private String message;
    private T data;

    public ApiResponse(T data) {
        this.status = HttpStatus.OK.value();
        this.message = "요청이 성공적으로 처리되었습니다";
        this.data = data;
    }
}