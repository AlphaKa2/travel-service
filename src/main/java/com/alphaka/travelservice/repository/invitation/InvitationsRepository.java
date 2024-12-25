package com.alphaka.travelservice.repository.invitation;

import com.alphaka.travelservice.entity.Invitations;
import com.alphaka.travelservice.entity.TravelPlans;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvitationsRepository extends JpaRepository<Invitations, Long> {

    List<Invitations> findByUserId(Long userId);

    Optional<Invitations> findByUserIdAndTravelPlans_TravelId(Long userId, Long travelId);

    boolean existsByUserIdAndTravelPlans(Long userId, TravelPlans travelPlan);
}
