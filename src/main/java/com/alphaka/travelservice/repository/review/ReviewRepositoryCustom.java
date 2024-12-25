package com.alphaka.travelservice.repository.review;

import com.alphaka.travelservice.dto.response.ReviewPlaceResponse;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepositoryCustom {

    // 여행 계획의 장소 정보 조회
    List<ReviewPlaceResponse> getReviewPlaces(Long travelId);
}
