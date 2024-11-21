package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.ParticipantChangeRequest;
import com.alphaka.travelservice.dto.response.ParticipantListDTO;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.InvitationNotFoundException;
import com.alphaka.travelservice.exception.custom.ParticipantAccessException;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.repository.invitation.ParticipantsRepository;
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
public class ParticipantService {
    private final JPAQueryFactory queryFactory;
    private final TravelPlansRepository travelPlansRepository;
    private final ParticipantsRepository participantsRepository;

    private final UserClient userClient;

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

    /**
     * 여행 계획 동행자 추가
     * @param userId
     * @param travelId
     * @return Long - 추가된 참여자 ID
     */
    @Transactional
    public Long addParticipant(Long userId, Long travelId) {
        log.info("여행 계획에 동행자 추가 userId: {}, travelId: {}", userId, travelId);

        // 여행 계획 조회
        TravelPlans travelPlan = travelPlansRepository.findById(travelId).orElseThrow(PlanNotFoundException::new);

        // 동행자 추가
        Participants participants = Participants.builder()
                .userId(userId)
                .travelPlans(travelPlan)
                .permission(Permission.VIEW)
                .build();

        participantsRepository.save(participants);
        log.info("동행자 추가 완료");

        return participants.getParticipantId();
    }

    /**
     * 여행지 참여자 목록 조회
     * @param currentUser
     * @param travelId
     * @return ParticipantListDTO - 참여자 목록 정보
     */
    public List<ParticipantListDTO> getParticipant(CurrentUser currentUser, Long travelId) {

        // Q 클래스 인스턴스 가져오기
        QParticipants participants = QParticipants.participants;
        QTravelPlans travelPlans = QTravelPlans.travelPlans;

        // 현재 사용자가 해당 여행지의 참여자인지 확인
        boolean isParticipant = queryFactory.selectFrom(participants)
                .where(participants.userId.eq(currentUser.getUserId())
                        .and(participants.travelPlans.travelId.eq(travelId)))
                .fetchFirst() != null;

        if (!isParticipant) {
            throw new ParticipantAccessException();
        }

        // 여행지에 연관된 참여자 조회
        List<Participants> result = queryFactory.selectFrom(participants)
                .join(participants.travelPlans, travelPlans).fetchJoin()
                .where(participants.travelPlans.travelId.eq(travelId))
                .fetch();

        // 참여자 정보 DTO 매핑
        List<ParticipantListDTO> participantDTOs = result.stream()
                .map(participant -> {
                    // 유저 정보 조회
                    ApiResponse<UserDTO> userResponse = userClient.findUserById(participant.getUserId());
                    // 유저 정보 접근
                    UserDTO userDTO = userResponse.getData();
                    String nickname = userDTO != null ? userDTO.getNickname() : null;

                    // 빌더로 DTO 생성
                    return ParticipantListDTO.builder()
                            .participantId(participant.getParticipantId())
                            .travelId(travelId)
                            .userId(participant.getUserId())
                            .nickname(nickname)
                            .permission(participant.getPermission())
                            .build();
                })
                .collect(Collectors.toList());

        return participantDTOs;
    }

    /**
     * 참여자 권한 변경
     * @param currentUser
     * @param request
     * @return Long - 변경된 참여자 ID
     */
    @Transactional
    public Long changeParticipant(CurrentUser currentUser, ParticipantChangeRequest request) {

        // 참여자 정보 조회
        Participants participant = participantsRepository.findById(request.getParticipantId())
                .orElseThrow(() -> new InvitationNotFoundException());

        // 여행지 권한 확인
        if (!currentUser.getUserId().equals(participant.getTravelPlans().getUserId())) {
            throw new ParticipantAccessException();
        }

        // 참여자 권한 변경
        participant.changeParticipantPermission(request.getPermission());

        // 변경된 참여자 아이디 반환
        return participant.getParticipantId();
    }

    /**
     * 참여자 삭제
     * @param currentUser
     * @param participantId
     * @return Long - 삭제된 참여자 ID
     */
    @Transactional
    public Long deleteParticipant(CurrentUser currentUser, Long participantId) {
        // 참여자 정보 조회
        Participants participant = participantsRepository.findById(participantId)
                .orElseThrow(() -> new InvitationNotFoundException());

        // 여행지 권한 확인
        if (!currentUser.getUserId().equals(participant.getTravelPlans().getUserId())) {
            throw new ParticipantAccessException();
        }

        // 참여자 삭제
        participantsRepository.delete(participant);

        // 삭제된 참여자 아이디 반환
        return participantId;
    }
}
