package com.alphaka.travelservice.repository;

import com.alphaka.travelservice.entity.TravelSchedules;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelSchedulesRepository extends JpaRepository<TravelSchedules, Long> {
}
