package com.alphaka.travelservice.repository;

import com.alphaka.travelservice.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewRepositoryCustom {

    // 특정 사용자와 여행 계획에 대한 리뷰가 존재하는지 확인
    boolean existsByUserIdAndTravelPlans_TravelId(Long userId, Long travelPlansId);
}
