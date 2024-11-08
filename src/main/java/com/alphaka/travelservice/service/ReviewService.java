package com.alphaka.travelservice.service;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.dto.request.ReviewDetailRequest;
import com.alphaka.travelservice.dto.response.ReviewPlaceResponse;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.*;
import com.alphaka.travelservice.repository.ParticipantsRepository;
import com.alphaka.travelservice.repository.ReviewRepository;
import com.alphaka.travelservice.repository.travel.TravelPlacesRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
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
    private final TravelPlansRepository travelPlansRepository;
    private final TravelPlacesRepository travelPlacesRepository;

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
                .orElseThrow(ParticipantNotFoundException::new);

        // 여행 계획의 상태가 완료인지 확인
        if (!participants.getTravelPlans().getTravelStatus().equals(TravelStatus.COMPLETED)) {
            throw new InvalidTravelStatusException();
        }

        // 여행 계획의 장소 정보 조회
        return reviewRepository.getReviewPlaces(travelId);
    }

    /**
     * 여행 리뷰 작성
     * @param currentUser - 현재 사용자
     * @param travelId - 여행 계획 ID
     * @param reviewDetails - 여행 리뷰 요청
     */
    @Transactional
    public void createReview(CurrentUser currentUser, Long travelId, List<ReviewDetailRequest> reviewDetails) {
        log.info("여행 리뷰 작성 - currentUser: {}, travelId: {}", currentUser, travelId);

        // 여행 계획 조회 및 존재 여부 확인
        TravelPlans travelPlans = travelPlansRepository.findById(travelId).orElseThrow(PlanNotFoundException::new);

        // 여행 계획의 participant 중 현재 사용자가 포함되어 있는지 확인
        boolean isParticipant = travelPlans.getParticipants().stream()
                .anyMatch(participant -> participant.getUserId().equals(currentUser.getUserId()));
        if (!isParticipant) {
            log.warn("해당 여행 계획에 참여하지 않은 사용자입니다.");
            throw new ParticipantNotFoundException();
        }

        // 여행 상태 확인
        if (!travelPlans.getTravelStatus().equals(TravelStatus.COMPLETED)) {
            log.warn("완료되지 않은 여행 계획에 대해 리뷰를 작성할 수 없습니다.");
            throw new InvalidTravelStatusException();
        }

        // 동일한 사용자와 여행 계획에 대한 리뷰가 이미 존재하는지 확인
        boolean exists = reviewRepository.existsByUserIdAndTravelPlans_TravelId(currentUser.getUserId(), travelId);
        if (exists) {
            log.warn("사용자가 이미 해당 여행 계획에 대한 리뷰를 작성했습니다.");
            throw new ReviewAlreadyWrittenException();
        }

        // 여행 스타일 정보 확인
        Long preferenceId = travelPlans.getPreferenceId();

        // 리뷰 생성
        Review review = Review.builder()
                .travelPlans(travelPlans)
                .preferenceId(preferenceId)
                .userId(currentUser.getUserId())
                .build();

        // 리뷰 상세 정보 생성
        reviewDetails.forEach(reviewDetailRequest -> {
            // 여행 장소 조회
            TravelPlaces travelPlace = travelPlacesRepository.findById(reviewDetailRequest.getPlace().getPlaceId())
                    .orElseThrow(PlanNotFoundException::new);

            // 리뷰 상세 정보
            ReviewDetail reviewDetail = ReviewDetail.builder()
                    .review(review)
                    .travelPlaces(travelPlace)
                    .rating(reviewDetailRequest.getRating())
                    .build();
            review.addReviewDetail(reviewDetail);
        });

        // 리뷰 저장
        reviewRepository.save(review);
        log.info("리뷰 저장 완료 - 리뷰 ID: {}", review.getId());
    }
}
