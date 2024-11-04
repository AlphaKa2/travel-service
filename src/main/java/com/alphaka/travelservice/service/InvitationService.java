package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.InvitationDTO;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.dto.response.InvitationListDTO;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.exception.custom.ResourceNotFoundException;
import com.alphaka.travelservice.repository.InvitationsRepository;
import com.alphaka.travelservice.repository.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final UserClient userClient;
    private final TravelPlansRepository travelPlansRepository;

    private final InvitationsRepository invitationsRepository;

    private final ParticipantService participantService;

    @Transactional
    public Long addInvitation(CurrentUser currentUser, ParticipantRequest request) {
        // Step 1: Retrieve user information by nickname
        ApiResponse<UserDTO> userResponse = userClient.findUserByNickname(request.getNickname());

        UserDTO userDTO = userResponse.getData();
        Long userId = userDTO.getUserId();

        // Step 2: Retrieve the travel plan by travelId
        TravelPlans travelPlan = (TravelPlans) travelPlansRepository.findById(request.getTravelId())
                .orElseThrow(() -> new PlanNotFoundException());

        // Step 4: Add the user as a participant in the travel plan
        Invitations newInvitation = new Invitations();
        newInvitation.setTravelPlans(travelPlan);
        newInvitation.setInvitationMessage("해당 여행 계획 제목");
        newInvitation.setStatus(InvitationStatus.PENDING);

        // Save the new participant entry
        invitationsRepository.save(newInvitation);

        return newInvitation.getInvitationId();
    }

    public List<InvitationListDTO> getInvitation(CurrentUser currentUser) {
        // Fetch the list of invitations for the current user
        List<Invitations> invitations = invitationsRepository.findByUserId(currentUser.getUserId());

        // Map each invitation to an InvitationListDTO
        List<InvitationListDTO> invitationDTOs = invitations.stream()
                .map(invitation -> {
                    InvitationListDTO dto = new InvitationListDTO();
                    dto.setInvitationId(invitation.getInvitationId()); // Assuming invitation has an ID
                    dto.setInvitationMessage(invitation.getInvitationMessage()); // Assuming invitation has a message field
                    return dto;
                })
                .collect(Collectors.toList());

        return invitationDTOs;
    }
    @Transactional
    public Long changeInvitation(CurrentUser currentUser, InvitationDTO invitationDTO) {

//        TravelPlans travelPlans = (TravelPlans) travelPlansRepository.findById(invitationDTO.getTravelId())
//                .orElseThrow(() -> new PlanNotFoundException());
        // Fetch the invitation by user and travel ID
        Invitations invitation = (Invitations) invitationsRepository.findByUserIdAndTravelPlans_TravelId(currentUser.getUserId(), invitationDTO.getTravelId())
                .orElseThrow(() -> new ResourceNotFoundException());

        // Update the invitation status with the value from the DTO
        invitation.setStatus(invitationDTO.getInvitationStatus());
        invitation.setUpdatedAt(LocalDateTime.now()); // Update timestamp if needed

        // Save the updated invitation
        invitationsRepository.save(invitation);

        // If the status is ACCEPTED, create ParticipantRequest and call addParticipant
        if (invitationDTO.getInvitationStatus() == InvitationStatus.ACCEPTED) {
            ParticipantRequest participantRequest = new ParticipantRequest();
            participantRequest.setTravelId(invitationDTO.getTravelId());
            participantRequest.setNickname(currentUser.getNickname()); // Set the nickname from the current user

            participantService.addParticipant(currentUser, participantRequest); // Pass ParticipantRequest object
        }

        return invitation.getInvitationId(); // Return the ID or any other relevant response
    }
}
