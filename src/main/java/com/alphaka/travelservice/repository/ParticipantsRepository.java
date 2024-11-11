package com.alphaka.travelservice.repository;

import com.alphaka.travelservice.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants, Long> {

    Optional<Participants> findByUserIdAndTravelPlans_TravelId(Long userId, Long travelId);

    List<Participants> findByTravelPlans_TravelId(Long travelId);

    boolean existsByUserIdAndTravelPlans_TravelId(Long userId, Long travelId);
}
