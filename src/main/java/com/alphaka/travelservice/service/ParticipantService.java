package com.alphaka.travelservice.service;

import com.alphaka.travelservice.entity.Participants;
import com.alphaka.travelservice.entity.Permission;
import com.alphaka.travelservice.entity.TravelPlans;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.repository.ParticipantsRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantService {

    private final TravelPlansRepository travelPlansRepository;
    private final ParticipantsRepository participantsRepository;

    /**
     * 여행 계획 생성시 자신을 동행자로 추가
     * @param userId - 사용자 ID
     * @param travelId - 여행 계획 ID
     */
    @Transactional
    public void addSelfParticipant(Long userId, Long travelId) {
        log.info("자신이 생성한 여행 계획에 동행자로 등록 userId: {}, travelId: {}", userId, travelId);

        // 여행 계획 조회
        TravelPlans travelPlan = travelPlansRepository.findById(travelId).orElseThrow(PlanNotFoundException::new);

        // 동행자 추가
        Participants participants = Participants.builder()
                .userId(userId)
                .travelPlans(travelPlan)
                .permission(Permission.EDIT)
                .build();

        participantsRepository.save(participants);
        log.info("동행자 추가 완료");
    }


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
}
