package com.alphaka.travelservice.repository.review;

import com.alphaka.travelservice.dto.response.ReviewPlaceResponse;
import com.alphaka.travelservice.entity.QTravelDays;
import com.alphaka.travelservice.entity.QTravelPlaces;
import com.alphaka.travelservice.entity.QTravelPlans;
import com.alphaka.travelservice.entity.QTravelSchedules;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    /**
     * 여행 계획의 장소 정보 조회
     * @param travelId - 여행 계획 ID
     * @return List<ReviewPlaceResponse> - 리뷰를 위한 장소 정보
     */
    @Override
    public List<ReviewPlaceResponse> getReviewPlaces(Long travelId) {
        QTravelPlans qPlan = QTravelPlans.travelPlans;
        QTravelDays qDay = QTravelDays.travelDays;
        QTravelSchedules qSchedule = QTravelSchedules.travelSchedules;
        QTravelPlaces qPlace = QTravelPlaces.travelPlaces;

        return queryFactory
                .select(Projections.constructor(ReviewPlaceResponse.class,
                        qPlace.placeId,
                        qPlace.placeName,
                        qPlace.address,
                        qPlace.latitude,
                        qPlace.longitude))
                .from(qPlan)
                .join(qPlan.travelDays, qDay)
                .join(qDay.travelSchedules, qSchedule)
                .join(qSchedule.place, qPlace)
                .where(qPlan.travelId.eq(travelId))
                .fetch();
    }
}
