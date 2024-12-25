package com.alphaka.travelservice.repository.travel;

import com.alphaka.travelservice.entity.TravelPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPlansRepository extends JpaRepository<TravelPlans, Long>, TravelPlansRepositoryCustom {

    // 여행 계획 리스트 조회
    List<TravelPlans> findByUserId(Long userId);
}
