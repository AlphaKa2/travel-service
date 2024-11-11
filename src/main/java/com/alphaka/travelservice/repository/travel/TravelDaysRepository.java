package com.alphaka.travelservice.repository.travel;

import com.alphaka.travelservice.entity.TravelDays;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelDaysRepository extends JpaRepository<TravelDays, Long> {
}
