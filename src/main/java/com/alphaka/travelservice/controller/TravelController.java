package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import com.alphaka.travelservice.service.TravelPlansService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travels")
@RequiredArgsConstructor
public class TravelController {

    private final TravelPlansService travelPlansService;

    // ai 추천된 여행 데이터 조회는 ai 서비스에서 호출

    /**
     * 여행 계획 생성
     * @param currentUser - 현재 사용자 정보
     * @param request - 여행 계획 생성 요청
     * @return ApiResponse<Long> - 생성된 여행 계획 ID
     */
    @PostMapping
    public ApiResponse<Long> createTravelPlan(CurrentUser currentUser,
                                              @Valid @RequestBody TravelPlanCreateRequest request) {
        Long response = travelPlansService.createTravelPlan(currentUser, request);
        return new ApiResponse<>(response);
    }

    /**
     * 여행 계획 목록 조회
     */

    /**
     * 여행 계획 상세 조회
     */

    /**
     * 여행 계획 수정
     */
//    @PutMapping("/travels")
//    public ApiResponse<Long> updateTravelByDTO(CurrentUser currentUser,
//                                               @Valid @RequestBody TravelUpdateRequest request) {
//        Long response = travelPlansService.updateTravelPlan(currentUser, request);
//        return new ApiResponse<>(response);
//    }

    /**
     * 여행 계획 삭제
     */

//    @GetMapping("/travels/{travelId}")
//        public ApiResponse<TravelReadRequest> readTravel(CurrentUser currentUser, @PathVariable("travelId") Long travelId) {
//        TravelReadRequest response = travelPlansService.readTravelPlan(currentUser, travelId);
//        return new ApiResponse<>(response);
//    }
//
//    @GetMapping("/travels")
//    public ApiResponse<List<TravelListDTO>> listTravel(CurrentUser currentUser) {
//        List<TravelListDTO> response = travelPlansService.listTravelPlan(currentUser);
//        return new ApiResponse<>(response);
//    }
//
//

//
//    @DeleteMapping("/travels/{travelId}")
//    public ApiResponse<Long> deleteTravelById(CurrentUser currentUser, @PathVariable("travelId") Long travelId) {
//        Long response = travelPlansService.deleteTravelPlan(currentUser, travelId);
//        return new ApiResponse<>(response);
//    }

//    @PostMapping("/travels/convert/{recommendation_trip_id}")
//    public ApiResponse<Long> convertTravel(CurrentUser currentUser, @PathVariable("recommendation_trip_id") String recommendation_trip_id) {
//        // Fetch recommendation data from AI service using Feign Client
//        ApiResponse<TravelPlanCreateRequest> recommendationResponse = aiClient.findTravelById(recommendation_trip_id);
//
//        // Ensure that the response contains data
//        if (recommendationResponse == null || recommendationResponse.getData() == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recommendation plan not found");
//        }
//
//        // Call the service to convert and save the travel plan
//        Long response = travelPlansService.convertTravelPlan(currentUser, recommendationResponse.getData());
//
//        return new ApiResponse<>(response);
//    }

}
