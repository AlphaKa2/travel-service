package com.alphaka.travelservice.repository.travel;

import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import com.alphaka.travelservice.dto.response.TravelPlanResponse;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPlansRepositoryCustom {

    /**
     * 여행 계획을 배치 삽입
     * @param userId - 사용자 ID
     * @param request - 여행 계획 생성 요청
     */
    Long batchInsertTravelPlan(Long userId, TravelPlanCreateRequest request);

    /**
     * 여행 계획 상세 조회
     * @param travelId - 여행 계획 ID
     */
    TravelPlanResponse getTravelPlanDetail(Long travelId);
}
