package com.alphaka.travelservice.repository;

import com.alphaka.travelservice.entity.TravelPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPlansRepository extends JpaRepository<TravelPlans, Long> {
    List<TravelPlans> findByUserId(Long userId);
}
