package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.AiClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.dto.request.ReviewDetailRequest;
import com.alphaka.travelservice.dto.response.PreferenceResponse;
import com.alphaka.travelservice.dto.response.ReviewPlaceResponse;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.InvalidTravelStatusException;
import com.alphaka.travelservice.exception.custom.ParticipantNotFoundException;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.exception.custom.ReviewAlreadyWrittenException;
import com.alphaka.travelservice.repository.data.TravelDataRepository;
import com.alphaka.travelservice.repository.invitation.ParticipantsRepository;
import com.alphaka.travelservice.repository.review.ReviewRepository;
import com.alphaka.travelservice.repository.travel.TravelPlacesRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final AiClient aiClient;
    private final TravelDataRepository travelDataRepository;
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

        // CDC를 위한 여행 정보 처리
        processReviewData(currentUser, travelPlans, review);
    }

    /**
     * 리뷰 데이터를 처리하여 TravelData 생성 및 저장
     * @param currentUser - 현재 사용자
     * @param travelPlans - 여행 계획
     * @param review - 리뷰
     */
    private void processReviewData(CurrentUser currentUser, TravelPlans travelPlans, Review review) {
        log.info("리뷰 데이터 처리 시작 - 리뷰 ID: {}", review.getId());

        // 성향 데이터 조회
        PreferenceResponse preferenceResponse = aiClient.getPreferenceData(travelPlans.getPreferenceId());
        log.info("성향 데이터 조회 완료 - preferenceId: {}", travelPlans.getPreferenceId());

        // 평점이 3점 이상인 리뷰 상세 정보 필터링
        List<ReviewDetail> filteredReviewDetails = review.getReviewDetails().stream()
                .filter(reviewDetail -> reviewDetail.getRating() >= 3)
                .collect(Collectors.toList());

        if (filteredReviewDetails.isEmpty()) {
            log.info("평점이 3점 이상인 리뷰가 없습니다. TravelData 저장을 생략합니다.");
            return;
        }

        // 각 리뷰 상세 정보에 대해 TravelData 생성 및 저장
        filteredReviewDetails.forEach(reviewDetail -> {
            TravelPlaces travelPlace = reviewDetail.getTravelPlaces();

            // TravelData 생성
            TravelData travelData = TravelData.builder()
                    .travelId(travelPlans.getTravelId())
                    .travelerId(currentUser.getUserId())
                    .travelPurpose(preferenceResponse.getPurposes())
                    .mvmnNm(preferenceResponse.getMeans_of_transportation())
                    .ageGrp(Integer.parseInt(preferenceResponse.getAge_group()))
                    .gender(preferenceResponse.getGender())
                    .travelStyl1(preferenceResponse.getStyle())
                    .travelMotive1(preferenceResponse.getMotive())
                    .travelStatusAccompany(preferenceResponse.getTravel_companion_status())
                    .travelStatusDays(preferenceResponse.getTravel_status_days())
                    .visitAreaNm(travelPlace.getPlaceName())
                    .roadAddr(travelPlace.getAddress())
                    .xCoord(travelPlace.getLatitude())
                    .yCoord(travelPlace.getLongitude())
                    .travelStatusYmd(formatTravelStatusYmd(travelPlans.getStartDate(), travelPlans.getEndDate()))
                    .esLoaded(0)
                    .build();

            // TravelData 저장
            travelDataRepository.save(travelData);
            log.info("TravelData 저장 완료 - travelDataId: {}", travelData.getTravelDataId());
        });
    }

    /**
     * 여행 상태를 YMD 형식으로 변환
     * @param startDate - 여행 시작일
     * @param endDate - 여행 종료일
     * @return String - YMD 형식의 여행 상태
     */
    private String formatTravelStatusYmd(LocalDate startDate, LocalDate endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return startDate.format(formatter) + "~" + endDate.format(formatter);
    }
}
