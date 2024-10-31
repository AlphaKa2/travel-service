package com.alphaka.travelservice.client;

import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 사용자 정보 조회를 위한 Feign Client
 */
@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    // 닉네임으로 사용자 조회
    @GetMapping("/users/info")
    ApiResponse<UserDTO> findUserByNickname(@RequestParam("nickname") String nickname);

    // ID로 사용자 조회
    @GetMapping("/users/info")
    ApiResponse<UserDTO> findUserById(@RequestParam("id") Long id);
}
