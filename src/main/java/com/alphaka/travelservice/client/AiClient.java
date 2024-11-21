package com.alphaka.travelservice.client;

import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.response.PreferenceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 여행 추천 정보 조회를 위한 Feign Client
 */
@FeignClient(name = "AI-SERVICE", url="ec2-13-125-174-132.ap-northeast-2.compute.amazonaws.com:8001")
public interface AiClient {

    /**
     * 추천된 여행의 성향 정보 조회
     * @param preferenceId - 성향 ID
     * @return PreferenceResponse - 성향 정보
     */
    @GetMapping("/preferences/{preference_id}")
    PreferenceResponse getPreferenceData(@PathVariable("preference_id") Long preferenceId);
}