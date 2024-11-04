package com.alphaka.travelservice.repository;

import com.alphaka.travelservice.entity.Participants;
import com.alphaka.travelservice.entity.TravelPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants, Long> {
}
