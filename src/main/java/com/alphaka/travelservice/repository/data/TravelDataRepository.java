package com.alphaka.travelservice.repository.data;

import com.alphaka.travelservice.entity.TravelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelDataRepository extends JpaRepository<TravelData, Long> {
}
