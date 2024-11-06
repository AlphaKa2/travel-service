package com.alphaka.travelservice.repository.travel;

import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPlansRepositoryCustom {

    /**
     * 여행 계획을 배치 삽입
     * @param userId - 사용자 ID
     * @param request - 여행 계획 생성 요청
     */
    Long batchInsertTravelPlan(Long userId, TravelPlanCreateRequest request);
}
