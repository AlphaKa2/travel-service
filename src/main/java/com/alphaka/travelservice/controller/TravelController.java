package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import com.alphaka.travelservice.dto.request.TravelPlanUpdateRequest;
import com.alphaka.travelservice.dto.response.TravelPlanListResponse;
import com.alphaka.travelservice.dto.response.TravelPlanResponse;
import com.alphaka.travelservice.service.TravelPlansService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
     * @param currentUser - 현재 사용자 정보
     * @return ApiResponse<List<TravelPlanListResponse>> - 여행 계획 목록
     */
    @GetMapping("/all")
    public ApiResponse<List<TravelPlanListResponse>> listTravel(CurrentUser currentUser) {
        List<TravelPlanListResponse> response = travelPlansService.getTravelPlanList(currentUser);
        return new ApiResponse<>(response);
    }

    /**
     * 여행 계획 상세 조회
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     * @return ApiResponse<TravelPlanResponse> - 여행 계획 상세 정보
     */
    @GetMapping("/{travelId}")
    public ApiResponse<TravelPlanResponse> getTravelPlanDetail(CurrentUser currentUser,
                                                               @PathVariable("travelId") Long travelId) {
        TravelPlanResponse response = travelPlansService.getTravelPlan(currentUser, travelId);
        return new ApiResponse<>(response);
    }

    /**
     * 여행 계획 수정
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     * @param request - 여행 계획 수정 요청
     * @return ApiResponse<Long> - 수정된 여행 계획 ID
     */
    @PutMapping("/{travelId}")
    public ApiResponse<Long> updateTravelPlan(CurrentUser currentUser,
                                              @PathVariable("travelId") Long travelId,
                                              @Valid @RequestBody TravelPlanUpdateRequest request) {
        Long response = travelPlansService.updateTravelPlan(currentUser, travelId, request);
        return new ApiResponse<>(response);
    }

    /**
     * 여행 계획 삭제
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     */
    @DeleteMapping("/{travelId}")
    public ApiResponse<Void> deleteTravelPlan(CurrentUser currentUser,
                                              @PathVariable("travelId") Long travelId) {
        travelPlansService.deleteTravelPlan(currentUser, travelId);
        return new ApiResponse<>(null);
    }
}
