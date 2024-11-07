package com.alphaka.travelservice.client;

import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

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

    /**
     * 사용자 ID 목록으로 사용자 조회 (user-service에서 결정되는 API Path에 따라 변경될 수 있음)
     * @param userIds - 사용자 ID 목록
     * @return ApiResponse<List<UserDTO>> - 사용자 정보 목록
     */
    @GetMapping("/users/{userId}/all")
    ApiResponse<List<UserDTO>> getUsersById(@PathVariable("userId") Set<Long> userIds);
}
