package com.alphaka.travelservice.service;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.dto.response.ReviewPlaceResponse;
import com.alphaka.travelservice.entity.Participants;
import com.alphaka.travelservice.entity.TravelStatus;
import com.alphaka.travelservice.exception.custom.UnauthorizedException;
import com.alphaka.travelservice.repository.ParticipantsRepository;
import com.alphaka.travelservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ParticipantsRepository participantsRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 생성을 위해 여행 계획의 장소 조회
     * @param currentUser - 현재 사용자
     * @param travelId - 여행 계획 ID
     * @return List<ReviewPlaceResponse> - 리뷰를 위한 장소 정보
     */
    @Transactional
    public List<ReviewPlaceResponse> getReviewPlaces(CurrentUser currentUser, Long travelId) {
        log.info("리뷰 작성을 위한 여행 계획 장소 조회 - currentUser: {}, travelId: {}", currentUser, travelId);

        // 여행 계획의 participant 중 현재 사용자가 포함되어 있는지 확인
        Participants participants = participantsRepository.findByUserIdAndTravelPlans_TravelId(currentUser.getUserId(), travelId)
                .orElseThrow(UnauthorizedException::new);

        // 여행 계획의 상태가 완료인지 확인
        if (!participants.getTravelPlans().getTravelStatus().equals(TravelStatus.COMPLETED)) {
            throw new UnauthorizedException();
        }

        // 여행 계획의 장소 정보 조회
        return reviewRepository.getReviewPlaces(travelId);
    }

}
