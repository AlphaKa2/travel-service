package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.ReviewDetailRequest;
import com.alphaka.travelservice.dto.response.ReviewPlaceResponse;
import com.alphaka.travelservice.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/travels/")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 생성을 위해 여행 계획의 장소 조회
     * @param currentUser - 현재 사용자
     * @param travelId - 여행 계획 ID
     * @return List<ReviewPlaceResponse> - 리뷰를 위한 장소 정보
     */
    @GetMapping("/{travelId}/reviews")
    public ApiResponse<List<ReviewPlaceResponse>> getReviewPlaces(CurrentUser currentUser,
                                                                 @PathVariable("travelId") Long travelId) {
        List<ReviewPlaceResponse> reviewPlace = reviewService.getReviewPlaces(currentUser, travelId);
        return new ApiResponse<>(reviewPlace);
    }

    /**
     * 여행 리뷰 작성
     * @param currentUser - 현재 사용자
     * @param travelId - 여행 계획 ID
     * @param reviewDetails - 여행 리뷰 상세 정보
     */
    @PostMapping("/{travelId}/reviews")
    public ApiResponse<Void> createReview(CurrentUser currentUser,
                                          @PathVariable("travelId") Long travelId,
                                          @Valid @RequestBody List<ReviewDetailRequest> reviewDetails) {
        reviewService.createReview(currentUser, travelId, reviewDetails);
        return new ApiResponse<>();
    }
}
