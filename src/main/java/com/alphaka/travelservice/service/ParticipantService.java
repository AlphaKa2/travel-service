//package com.alphaka.travelservice.service;
//
//import com.alphaka.travelservice.client.UserClient;
//import com.alphaka.travelservice.common.dto.CurrentUser;
//import com.alphaka.travelservice.common.dto.UserDTO;
//import com.alphaka.travelservice.common.response.ApiResponse;
//import com.alphaka.travelservice.dto.request.ParticipantChangeRequest;
//import com.alphaka.travelservice.dto.request.ParticipantRequest;
//import com.alphaka.travelservice.dto.response.ParticipantListDTO;
//import com.alphaka.travelservice.entity.Participants;
//import com.alphaka.travelservice.entity.Permission;
//import com.alphaka.travelservice.entity.TravelPlans;
//import com.alphaka.travelservice.exception.custom.InvitationNotFoundException;
//import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
//import com.alphaka.travelservice.repository.ParticipantsRepository;
//import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class ParticipantService {
//
//    private final UserClient userClient;
//    private final TravelPlansRepository travelPlansRepository;
//    private final ParticipantsRepository participantsRepository;
//
//
//    @Transactional
//    public Long addParticipant(CurrentUser currentUser, ParticipantRequest request) {
//        // Step 1: Retrieve user information by nickname
//        ApiResponse<UserDTO> userResponse = userClient.findUserByNickname(request.getNickname());
//
//        UserDTO userDTO = userResponse.getData();
//        Long userId = userDTO.getUserId();
//
//        // Step 2: Retrieve the travel plan by travelId
//        TravelPlans travelPlan = (TravelPlans) travelPlansRepository.findById(request.getTravelId())
//                .orElseThrow(() -> new PlanNotFoundException());
//
//        // Step 4: Add the user as a participant in the travel plan
//        Participants participant = new Participants();
//        participant.setTravelPlans(travelPlan);
//        participant.setUserId(userId);
//        participant.setPermission(Permission.VIEW); // Default permission
//        participant.setJoinedAt(LocalDateTime.now());
//
//        // Save the new participant entry
//        participantsRepository.save(participant);
//
//        return participant.getParticipantId();
//    }
//
//    public List<ParticipantListDTO> getParticipant(CurrentUser currentUser, Long travelId) {
//        // Fetch participants associated with the given travelId
//        List<Participants> participants = participantsRepository.findByTravelPlans_TravelId(travelId);
//
//        // Map participants to ParticipantListDTO and retrieve the nickname for each userId
//        List<ParticipantListDTO> participantDTOs = participants.stream().map(participant -> {
//            // Fetch user details using UserClient
//            ApiResponse<UserDTO> userResponse = userClient.findUserById(participant.getUserId());
//
//            // Check if user data is present
//            UserDTO userDTO = userResponse.getData();
//            String nickname = userDTO != null ? userDTO.getNickname() : null;
//
//            // Map to DTO
//            ParticipantListDTO dto = new ParticipantListDTO();
//            dto.setParticipantId(participant.getParticipantId());
//            dto.setTravelId(travelId);
//            dto.setUserId(participant.getUserId());
//            dto.setNickname(nickname); // Set the fetched nickname
//            dto.setPermission(participant.getPermission());
//            return dto;
//        }).collect(Collectors.toList());
//
//        return participantDTOs;
//    }
//
//    @Transactional
//    public Long changeParticipant(CurrentUser currentUser, ParticipantChangeRequest request) {
//        // Fetch the participant using the participantId from the request
//        Participants participant = participantsRepository.findById(request.getParticipantId())
//                .orElseThrow(() -> new InvitationNotFoundException());
//
//        // Update the permission with the value from the DTO
//        participant.setPermission(request.getPermission());
//
//        // Save the updated participant
//        participantsRepository.save(participant);
//
//        // Return the participant ID or other relevant information if needed
//        return participant.getParticipantId();
//    }
//
//    @Transactional
//    public Long deleteParticipant(CurrentUser currentUser, Long participantId) {
//        // Check if the participant exists
//        Participants participant = participantsRepository.findById(participantId)
//                .orElseThrow(() -> new InvitationNotFoundException());
//
//        // 추가적으로 해당 요청한 유저가 TravelPlan의 owner인지 확인 필요
//
//        // Delete the participant
//        participantsRepository.delete(participant);
//
//        // Return the ID of the deleted participant
//        return participantId;
//    }
//}
