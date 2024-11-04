package com.alphaka.travelservice.client;

import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.TravelCreateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 여행 추천 정보 조회를 위한 Feign Client
 */
//@FeignClient(name = "PLAN-SERVICE")  // Ensure to set the correct AI service URL
//public interface AiClient {
//
//    // Fetch recommendation details by ID
//    @GetMapping("/recommendations/{recommendation_trip_id}")
//    ApiResponse<TravelCreateRequest> findTravelById(@PathVariable("recommendation_trip_id") String recommendation_trip_id);
//}