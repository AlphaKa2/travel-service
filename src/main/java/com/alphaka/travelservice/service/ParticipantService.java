package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.entity.Participants;
import com.alphaka.travelservice.entity.Permission;
import com.alphaka.travelservice.entity.TravelPlans;
import com.alphaka.travelservice.exception.custom.ParticipantNotFoundException;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.repository.ParticipantsRepository;
import com.alphaka.travelservice.repository.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final UserClient userClient;
    private final TravelPlansRepository travelPlansRepository;
    private final ParticipantsRepository participantsRepository;


    @Transactional
    public Long addParticipant(CurrentUser currentUser, ParticipantRequest request) {
        // Step 1: Retrieve user information by nickname
        ApiResponse<UserDTO> userResponse = userClient.findUserByNickname(request.getNickname());

        UserDTO userDTO = userResponse.getData();
        Long userId = userDTO.getUserId();

        // Step 2: Retrieve the travel plan by travelId
        TravelPlans travelPlan = (TravelPlans) travelPlansRepository.findById(request.getTravelId())
                .orElseThrow(() -> new PlanNotFoundException());

        // Step 4: Add the user as a participant in the travel plan
        Participants participant = new Participants();
        participant.setTravelPlans(travelPlan);
        participant.setUserId(userId);
        participant.setPermission(Permission.VIEW); // Default permission
        participant.setJoinedAt(LocalDateTime.now());

        // Save the new participant entry
        participantsRepository.save(participant);

        return participant.getParticipantId();
    }
}
