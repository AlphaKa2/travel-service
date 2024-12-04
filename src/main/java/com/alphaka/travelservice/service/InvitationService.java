package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.InvitationDTO;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.dto.response.InvitationListDTO;
import com.alphaka.travelservice.dto.response.InvitedListDTO;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.*;
import com.alphaka.travelservice.repository.invitation.InvitationsRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvitationService {

    private final JPAQueryFactory queryFactory;

    private final UserClient userClient;
    private final TravelPlansRepository travelPlansRepository;

    private final InvitationsRepository invitationsRepository;

    private final ParticipantService participantService;

    /**
     * 동행자 초대
     * @param currentUser
     * @param request
     * @return Long - 생성된 초대 ID
     */
    @Transactional
    public Long addInvitation(CurrentUser currentUser, ParticipantRequest request) {

        log.info("사용자 초대 시작. 현재 사용자: {}", currentUser.getNickname());

        // 닉네임이 비어있는지 확인
        if (request.getNickname() == null || request.getNickname().isEmpty()) {
            throw new UserNotFoundException();
        }

        // 유저 정보 조회
        ApiResponse<UserDTO> userResponse = userClient.findUserByNickname(request.getNickname());
        UserDTO userDTO = userResponse.getData();
        Long userId = userDTO.getUserId();

        // userId가 없는 경우 확인
        if (userDTO == null || userDTO.getUserId() == null) {
            throw new UserNotFoundException();
        }

        System.out.println("Requested Travel ID: " + request.getTravelId());
        // 여행지 정보 조회
        TravelPlans travelPlan = (TravelPlans) travelPlansRepository.findById(request.getTravelId())
                .orElseThrow(() -> new PlanNotFoundException());

        // 여행지 권한 확인
        if (!currentUser.getUserId().equals(travelPlan.getUserId())) {
            throw new InvitationAccessException();
        }

        // 중복 초대 확인
        boolean isDuplicate = invitationsRepository.existsByUserIdAndTravelPlans(userId, travelPlan);
        if (isDuplicate) {
            throw new DuplicateInvitationException();
        }

        // 동행자 초대 생성
        Invitations newInvitation = Invitations.builder()
                .travelPlans(travelPlan)
                .userId(userId)
                .invitationMessage(travelPlan.getTravelName())
                .build();

        invitationsRepository.save(newInvitation);

        log.info("사용자 초대 완료");
        return newInvitation.getInvitationId();
    }

    /**
     * 초대 받은 목록 조회
     * @param currentUser
     * @return InvitationListDTO - 초대 목록 정보
     */
    public List<InvitationListDTO> getInvitation(CurrentUser currentUser) {
        // Q 클래스 인스턴스 가져오기
        QInvitations invitations = QInvitations.invitations;
        QTravelPlans travelPlans = QTravelPlans.travelPlans;

        // QueryDSL을 사용한 쿼리
        List<Invitations> result = queryFactory.selectFrom(invitations)
                .join(invitations.travelPlans, travelPlans).fetchJoin()
                .where(invitations.userId.eq(currentUser.getUserId()))
                .fetch();

        // DTO로 매핑
        List<InvitationListDTO> invitationDTOs = result.stream()
                .map(invitation -> InvitationListDTO.builder()
                        .travelId(invitation.getTravelPlans().getTravelId())
                        .invitationId(invitation.getInvitationId())
                        .invitationMessage(invitation.getInvitationMessage())
                        .invitationStatus(invitation.getStatus())
                        .build())
                .collect(Collectors.toList());

        return invitationDTOs;
    }

    /**
     * 초대 상태 변경
     * @param currentUser
     * @param invitationDTO
     * @return Long - 변경된 초대 ID
     */
    @Transactional
    public Long changeInvitation(CurrentUser currentUser, InvitationDTO invitationDTO) {

        log.info("사용자 초대 상태 변경 시작. 현재 사용자: {}", currentUser.getNickname());
        // 초대 정보 조회
        Invitations invitation = (Invitations) invitationsRepository.findByUserIdAndTravelPlans_TravelId(currentUser.getUserId(), invitationDTO.getTravelId())
                .orElseThrow(() -> new InvitationNotFoundException());

        // 초대 상태 변경
        invitation.changeInvitationStatus(invitationDTO.getInvitationStatus());

        invitationsRepository.save(invitation);

        // 참여자 추가
        if (invitationDTO.getInvitationStatus() == InvitationStatus.ACCEPTED) {
            participantService.addParticipant(currentUser.getUserId(), invitationDTO.getTravelId());
        }

        log.info("사용자 초대 상태 변경 완료");

        return invitation.getInvitationId();
    }

    /**
     * 초대한 사용자 목록 조회
     * @param currentUser
     * @param travelId
     * @return InvitedListDTO - 초대한 사용자 목록 정보
     */
    public List<InvitedListDTO> getInvited(CurrentUser currentUser, Long travelId) {

        // 여행지 정보 조회
        TravelPlans travelPlan = (TravelPlans) travelPlansRepository.findById(travelId)
                .orElseThrow(() -> new PlanNotFoundException());

        // 여행지 권한 확인
        if (!currentUser.getUserId().equals(travelPlan.getUserId())) {
            throw new InvitationAccessException();
        }

        // Q 클래스 인스턴스 가져오기
        QInvitations invitations = QInvitations.invitations;
        QTravelPlans travelPlans = QTravelPlans.travelPlans;

        // QueryDSL을 사용한 쿼리
        List<Invitations> result = queryFactory.selectFrom(invitations)
                .join(invitations.travelPlans, travelPlans).fetchJoin()
                .where(travelPlans.travelId.eq(travelId)) // Match invitations by travelId
                .fetch();

        // DTO로 매핑
        List<InvitedListDTO> invitedDTOs = result.stream()
                .map(invitation -> {
                    // Call FeignClient to get user information by ID
                    ApiResponse<UserDTO> userResponse = userClient.findUserById(invitation.getUserId());
                    UserDTO userDTO = userResponse.getData();

                    return InvitedListDTO.builder()
                            .travelId(invitation.getTravelPlans().getTravelId())
                            .invitationId(invitation.getInvitationId())
                            .invitationMessage(invitation.getInvitationMessage())
                            .invitationStatus(invitation.getStatus())
                            .nickname(userDTO.getNickname()) // Use data from UserDTO
                            .userId(userDTO.getUserId()) // Include userId from UserDTO if needed
                            .build();
                })
                .collect(Collectors.toList());

        return invitedDTOs;
    }
}
