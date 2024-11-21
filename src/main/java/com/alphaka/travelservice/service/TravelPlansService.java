package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.dto.request.*;
import com.alphaka.travelservice.dto.response.TravelPlanListResponse;
import com.alphaka.travelservice.dto.response.TravelPlanResponse;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.*;
import com.alphaka.travelservice.repository.invitation.ParticipantsRepository;
import com.alphaka.travelservice.repository.travel.TravelPlacesRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlansService {

    private final UserClient userClient;
    private final TravelPlansRepository travelPlansRepository;
    private final ParticipantsRepository participantsRepository;
    private final ParticipantService participantService;
    private final TravelPlacesRepository travelPlacesRepository;

    /**
     * 여행 계획 목록 조회
     * @param currentUser - 현재 사용자 정보
     * @return List<TravelPlanListResponse> - 여행 계획 목록
     */
    public List<TravelPlanListResponse> getTravelPlanList(CurrentUser currentUser) {
        log.info("여행 계획 목록 조회 시작. 현재 사용자: {}", currentUser.getNickname());

        // 여행 계획 목록 조회
        List<TravelPlanListResponse> travelPlanList = travelPlansRepository.getTravelPlanList(currentUser.getUserId());

        // 여행 동행자 ID 목록 가져오기
        Set<Long> allParticipantIds = travelPlanList.stream()
                .flatMap(plan -> plan.getParticipants().stream())
                .map(Long::valueOf) // 임시로 ID를 문자열로 저장했으므로 Long으로 변환
                .collect(Collectors.toSet());

        if (!allParticipantIds.isEmpty()) {
            // 동행자 정보 가져오기
            List<UserDTO> participantsInfo = userClient.getUsersById(allParticipantIds).getData();

            // ID와 닉네임 매핑
            Map<Long, String> participantIdToNickname = participantsInfo.stream()
                    .collect(Collectors.toMap(UserDTO::getUserId, UserDTO::getNickname));

            // TravelPlanListResponse의 participants 리스트를 닉네임으로 변환
            for (TravelPlanListResponse plan : travelPlanList) {
                List<String> nicknames = plan.getParticipants().stream()
                        .map(idStr -> participantIdToNickname.getOrDefault(Long.valueOf(idStr), "Unknown"))
                        .collect(Collectors.toList());
                plan.setParticipants(nicknames);
            }
        }

        return travelPlanList;
    }

    /**
     * 여행 계획 상세 조회
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     * @return TravelPlanResponse - 여행 계획 상세 정보
     */
    public TravelPlanResponse getTravelPlan(CurrentUser currentUser, Long travelId) {
        log.info("여행 계획 상세 조회 시작. 현재 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);

        // 여행 계획 조회
        TravelPlanResponse travelPlan = travelPlansRepository.getTravelPlanDetail(travelId);
        if (travelPlan == null) {
            log.warn("여행 계획을 찾을 수 없습니다. 여행 계획 ID: {}", travelId);
            throw new PlanNotFoundException();
        }


        // 여행 참가자 목록 id 리스트로 변환
        Set<Long> participantsList = travelPlansRepository.findById(travelId)
                .orElseThrow(ParticipantNotFoundException::new)
                .getParticipants()
                .stream()
                .map(Participants::getUserId)
                .collect(Collectors.toSet());

        // 현재 사용자가 참가자 목록에 있는지 확인
        if (!participantsList.contains(currentUser.getUserId())) {
            log.warn("사용자가 여행 참가자 목록에 없습니다. 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);
            throw new ParticipantNotFoundException();
        }

        // 참가자 닉네임 목록 설정
        List<UserDTO> participantsInfo = userClient.getUsersById(participantsList).getData();
        travelPlan.setParticipants(participantsInfo.stream()
                .map(UserDTO::getNickname)
                .collect(Collectors.toList()));


        // lastUpdatedBy 설정
        String lastUpdatedByNickname = userClient.findUserById(travelPlansRepository.findById(travelId)
                        .orElseThrow(PlanNotFoundException::new)
                        .getLastUpdatedBy())
                .getData()
                .getNickname();
        travelPlan.setLastUpdatedBy(lastUpdatedByNickname);

        return travelPlan;
    }

    /**
     * 여행 계획 생성
     * @param currentUser - 현재 사용자 정보
     * @param request - 여행 계획 생성 요청
     * @return Long - 생성된 여행 계획 ID
     */
    @Transactional
    public Long createTravelPlan(CurrentUser currentUser, TravelPlanCreateRequest request) {
        log.info("여행 계획 생성 시작. 현재 사용자: {}", currentUser.getNickname());

        // 여행 일수 검증
        validateTravelDaysForCreate(request.getStartDate(), request.getEndDate(), request.getDays());

        // 여행 계획 생성
        log.info("여행 계획 배치 삽입 시작");
        Long travelId = travelPlansRepository.batchInsertTravelPlan(currentUser.getUserId(), request);

        // 동행에 자기 자신 추가
        participantService.addSelfParticipant(currentUser.getUserId(), travelId);

        return travelId;
    }

    /**
     * 여행 계획 업데이트
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     * @param request - 여행 계획 업데이트 요청
     * @return Long - 업데이트된 여행 계획 ID
     */
    @Transactional
    public Long updateTravelPlan(CurrentUser currentUser, Long travelId, TravelPlanUpdateRequest request) {
        // 기존 여행 계획 조회
        TravelPlans existingTravelPlan = travelPlansRepository.findById(travelId)
                .orElseThrow(PlanNotFoundException::new);

        // 사용자 수정 권한 확인
        Participants participants = participantsRepository.findByUserIdAndTravelPlans_TravelId(currentUser.getUserId(), travelId)
                .orElseThrow(ParticipantNotFoundException::new);
        if (!participants.getPermission().equals(Permission.EDIT)) {
            log.warn("사용자에게 수정 권한이 없습니다. 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);
            throw new UnauthorizedException();
        }

        // 여행 계획 정보 업데이트
        existingTravelPlan.updateTravelPlans(
                request.getTravelName(),
                request.getDescription(),
                request.getStartDate(),
                request.getEndDate(),
                currentUser.getUserId()
        );

        // 여행 일자 업데이트
        updateTravelDays(existingTravelPlan, request);

        return existingTravelPlan.getTravelId();
    }

    /**
     * 여행 계획 삭제
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     */
    @Transactional
    public void deleteTravelPlan(CurrentUser currentUser, Long travelId) {
        // 여행 계획 조회
        TravelPlans travelPlan = travelPlansRepository.findById(travelId)
                .orElseThrow(PlanNotFoundException::new);

        // 사용자 삭제 권한 확인
        if (!travelPlan.getUserId().equals(currentUser.getUserId())) {
            log.warn("사용자에게 삭제 권한이 없습니다. 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);
            throw new UnauthorizedException();
        }

        // 여행 계획 삭제
        travelPlansRepository.delete(travelPlan);
    }

    /**
     * 여행 계획의 상태 업데이트
     * @param currentUser - 현재 사용자 정보
     * @param travelId - 여행 계획 ID
     * @param status - 업데이트할 여행 계획 상태
     */
    @Transactional
    public void updateTravelPlanStatus(CurrentUser currentUser, Long travelId, String status) {
        log.info("여행 계획 상태 업데이트 시작. 현재 사용자: {}, 여행 계획 ID: {}, 상태: {}", currentUser.getNickname(), travelId, status);

        // 여행 계획을 생성한 사용자인지 확인
        TravelPlans travelPlan = travelPlansRepository.findById(travelId)
                .orElseThrow(PlanNotFoundException::new);
        if (!travelPlan.getUserId().equals(currentUser.getUserId())) {
            log.warn("사용자에게 상태 업데이트 권한이 없습니다. 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);
            throw new UnauthorizedException();
        }

        // 상태 검증
        if (!TravelStatus.valueOf(status).equals(TravelStatus.PLANNED) &&
            !TravelStatus.valueOf(status).equals(TravelStatus.COMPLETED)) {
            log.warn("유효하지 않은 여행 계획 상태입니다. 상태: {}", status);
            throw new InvalidTravelStatusException();
        }

        // 여행 계획 상태 업데이트
        travelPlan.changeTravelStatus(TravelStatus.valueOf(status));
    }

    /**
     * 여행 일자 업데이트
     * @param existingTravelPlan - 기존 여행 계획 엔티티
     * @param request - 여행 계획 업데이트 요청
     */
    private void updateTravelDays(TravelPlans existingTravelPlan, TravelPlanUpdateRequest request) {
        // 업데이트 요청에서 제공된 TravelDayId 수집
        Set<Long> requestTravelDayIds = request.getDays().stream()
                .map(TravelDayUpdateRequest::getTravelDayId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 여행 일자 업데이트 또는 생성
        for (TravelDayUpdateRequest dayRequest : request.getDays()) {
            if (dayRequest.getTravelDayId() != null) {
                // 기존 일자 업데이트
                TravelDays existingDay = existingTravelPlan.getTravelDays().stream()
                        .filter(day -> day.getDayId().equals(dayRequest.getTravelDayId()))
                        .findFirst()
                        .orElseThrow(InvalidTravelDayException::new);
                updateExistingDay(existingDay, existingTravelPlan, dayRequest);
            } else {
                // 새로운 일자 추가
                createNewDay(existingTravelPlan, dayRequest);
            }
        }

        // 업데이트 요청에 없는 기존 일자 삭제
        deleteRemovedDays(existingTravelPlan, requestTravelDayIds);
    }

    /**
     * 기존 여행 일자 업데이트 메서드
     * @param existingDay - 기존 여행 일자 엔티티
     * @param existingTravelPlan - 기존 여행 계획 엔티티
     * @param dayRequest - 여행 일자 업데이트 요청
     */
    private void updateExistingDay(TravelDays existingDay, TravelPlans existingTravelPlan, TravelDayUpdateRequest dayRequest) {
        existingDay.updateTravelDays(
                existingTravelPlan,
                dayRequest.getDayNumber(),
                dayRequest.getDate()
        );

        // 스케줄 업데이트 및 추가
        updateTravelSchedules(existingDay, dayRequest);
    }

    /**
     * 새로운 여행 일자 생성 메서드
     * @param existingTravelPlan - 기존 여행 계획 엔티티
     * @param dayRequest - 여행 일자 업데이트 요청
     */
    private void createNewDay(TravelPlans existingTravelPlan, TravelDayUpdateRequest dayRequest) {
        TravelDays newDay = TravelDays.builder()
                .travelPlans(existingTravelPlan)
                .dayNumber(dayRequest.getDayNumber())
                .date(dayRequest.getDate())
                .build();
        existingTravelPlan.getTravelDays().add(newDay);

        // 스케줄 생성
        for (TravelScheduleUpdateRequest scheduleRequest : dayRequest.getSchedules()) {
            createNewSchedule(newDay, scheduleRequest);
        }
    }

    /**
     * 여행 스케줄 업데이트 메서드
     * @param existingDay - 여행 일자 엔티티
     * @param dayRequest - 여행 일자 업데이트 요청
     */
    private void updateTravelSchedules(TravelDays existingDay, TravelDayUpdateRequest dayRequest) {
        // 업데이트 요청에서 제공된 TravelScheduleId 수집
        Set<Long> requestTravelScheduleIds = dayRequest.getSchedules().stream()
                .map(TravelScheduleUpdateRequest::getTravelScheduleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        for (TravelScheduleUpdateRequest scheduleRequest : dayRequest.getSchedules()) {
            if (scheduleRequest.getTravelScheduleId() != null) {
                // 기존 스케줄 업데이트
                TravelSchedules existingSchedule = existingDay.getTravelSchedules().stream()
                                .filter(schedule -> schedule.getScheduleId().equals(scheduleRequest.getTravelScheduleId()))
                                .findFirst()
                                .orElseThrow(InvalidTravelScheduleException::new);
                existingSchedule.updateTravelSchedules(
                        existingDay,
                        scheduleRequest.getOrder(),
                        scheduleRequest.getStartTime(),
                        scheduleRequest.getEndTime()
                );
                updateTravelPlace(existingSchedule, scheduleRequest.getPlace());
            } else {
                // 새로운 스케줄 추가
                createNewSchedule(existingDay, scheduleRequest);
            }
        }

        // 업데이트 요청에 없는 기존 스케줄 삭제
        deleteRemovedSchedules(existingDay, requestTravelScheduleIds);
    }

    /**
     * 새로운 스케줄 생성 메서드
     * @param day - 여행 일자 엔티티
     * @param scheduleRequest - 스케줄 업데이트 요청
     */
    private void createNewSchedule(TravelDays day, TravelScheduleUpdateRequest scheduleRequest) {
        TravelSchedules newSchedule = TravelSchedules.builder()
                .travelDays(day)
                .scheduleOrder(scheduleRequest.getOrder())
                .startTime(scheduleRequest.getStartTime())
                .endTime(scheduleRequest.getEndTime())
                .build();
        day.getTravelSchedules().add(newSchedule);

        // 새로운 장소 추가
        TravelPlaces newPlace = TravelPlaces.builder()
                .travelSchedules(newSchedule)
                .placeName(scheduleRequest.getPlace().getPlaceName())
                .address(scheduleRequest.getPlace().getAddress())
                .latitude(scheduleRequest.getPlace().getLatitude())
                .longitude(scheduleRequest.getPlace().getLongitude())
                .build();
        newSchedule.setPlace(newPlace);
    }

    /**
     * 삭제된 여행 일자 제거 메서드
     * @param existingTravelPlan - 기존 여행 계획 엔티티
     * @param requestTravelDayIds - 요청된 여행 일자 ID 집합
     */
    private void deleteRemovedDays(TravelPlans existingTravelPlan, Set<Long> requestTravelDayIds) {
        List<TravelDays> daysToRemove = existingTravelPlan.getTravelDays().stream()
                .filter(day -> day.getDayId() != null && !requestTravelDayIds.contains(day.getDayId()))
                .collect(Collectors.toList());

        for (TravelDays day : daysToRemove) {
            log.info("삭제할 여행 일자: dayId={}", day.getDayId());
            existingTravelPlan.getTravelDays().remove(day);
            // TravelSchedules 및 TravelPlaces는 orphanRemoval=true 설정으로 인해 자동 삭제
        }
    }

    /**
     * 삭제된 스케줄 제거 메서드
     * @param existingDay - 여행 일자 엔티티
     * @param requestTravelScheduleIds - 요청된 스케줄 ID 집합
     */
    private void deleteRemovedSchedules(TravelDays existingDay, Set<Long> requestTravelScheduleIds) {
        List<TravelSchedules> schedulesToRemove = existingDay.getTravelSchedules().stream()
                .filter(schedule -> schedule.getScheduleId() != null && !requestTravelScheduleIds.contains(schedule.getScheduleId()))
                .collect(Collectors.toList());

        for (TravelSchedules schedule : schedulesToRemove) {
            log.info("삭제할 여행 스케줄: scheduleId={}", schedule.getScheduleId());
            existingDay.getTravelSchedules().remove(schedule);
            // TravelPlaces는 orphanRemoval=true 설정으로 인해 자동 삭제
        }
    }

    /**
     * 여행 장소 업데이트 메서드
     * @param travelSchedule - 스케줄 엔티티
     * @param placeRequest - 장소 업데이트 요청
     */
    private void updateTravelPlace(TravelSchedules travelSchedule, TravelPlaceUpdateRequest placeRequest) {
        if (placeRequest.getTravelPlaceId() != null) {
            // 기존 장소 업데이트
            TravelPlaces existingPlace = travelPlacesRepository.findById(placeRequest.getTravelPlaceId())
                    .orElseThrow(PlanNotFoundException::new);

            // 장소가 현재 스케줄과 연결되어 있는지 확인
            if (!existingPlace.getTravelSchedules().equals(travelSchedule)) {
                throw new InvalidTravelScheduleException();
            }

            // 장소 정보 업데이트
            existingPlace.updateTravelPlaces(
                    travelSchedule,
                    placeRequest.getPlaceName(),
                    placeRequest.getAddress(),
                    placeRequest.getLatitude(),
                    placeRequest.getLongitude()
            );
        } else {
            // 새로운 장소 추가
            TravelPlaces newPlace = TravelPlaces.builder()
                    .travelSchedules(travelSchedule)
                    .placeName(placeRequest.getPlaceName())
                    .address(placeRequest.getAddress())
                    .latitude(placeRequest.getLatitude())
                    .longitude(placeRequest.getLongitude())
                    .build();
            travelSchedule.setPlace(newPlace);
        }
    }

    /**
     * 여행 계획 생성 시 일수 검증
     * @param startDate - 여행 시작일
     * @param endDate - 여행 종료일
     * @param days - 여행 일정
     */
    private void validateTravelDaysForCreate(LocalDate startDate, LocalDate endDate, List<?> days) {
        log.info("여행 일수 검증 시작");

        // 여행 일정에 따른 두 날짜 사이의 여행 일수 계산 (시작일과 종료일 포함)
        long expectedDayCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (days.size() != expectedDayCount) {
            log.warn("여행 일수가 일치하지 않습니다. 예상 일수: {}, 입력된 일수: {}", expectedDayCount, days.size());
            throw new InvalidTravelDayException();
        }

        // 각 TravelDayRequest의 날짜와 일수 확인
        for (int i = 0; i < days.size(); i++) {
            // TravelDayRequest 타입으로 캐스팅
            TravelDayRequest dayRequest = (TravelDayRequest) days.get(i);
            LocalDate expectedDate = startDate.plusDays(i);

            // 입력한 여행 날짜가 예상 날짜와 일치하는지 확인
            if (!dayRequest.getDate().equals(expectedDate)) {
                log.warn("여행 일자가 예상 일자와 일치하지 않습니다. 여행 일자: {}, 예상 일자: {}", dayRequest.getDate(), expectedDate);
                throw new InvalidTravelDayException();
            }

            // 입력한 여행 날짜가 여행 일수의 날짜와 일치하는지 확인
            if (dayRequest.getDayNumber() != (i + 1)) {
                log.warn("여행 일수가 예상 일수와 일치하지 않습니다. 여행 일수: {}, 예상 일수: {}", dayRequest.getDayNumber(), i + 1);
                throw new InvalidTravelDayException();
            }
        }
    }

    /**
     * 여행 계획 업데이트 시 검증
     * @param startDate - 여행 시작일
     * @param endDate - 여행 종료일
     * @param days - 여행 일정
     */
    private void validateTravelDaysForUpdate(LocalDate startDate, LocalDate endDate, List<?> days) {
        log.info("여행 일수 검증 시작");

        // 여행 일정에 따른 두 날짜 사이의 여행 일수 계산 (시작일과 종료일 포함)
        long expectedDayCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;

        if (days.size() != expectedDayCount) {
            log.warn("여행 일수가 일치하지 않습니다. 예상 일수: {}, 입력된 일수: {}", expectedDayCount, days.size());
            throw new InvalidTravelDayException();
        }

        // 각 TravelDayRequest의 날짜와 일수 확인
        for (int i = 0; i < days.size(); i++) {
            // TravelDayRequest 타입으로 캐스팅
            TravelDayUpdateRequest dayRequest = (TravelDayUpdateRequest) days.get(i);
            LocalDate expectedDate = startDate.plusDays(i);

            // 입력한 여행 날짜가 예상 날짜와 일치하는지 확인
            if (!dayRequest.getDate().equals(expectedDate)) {
                log.warn("여행 일자가 예상 일자와 일치하지 않습니다. 여행 일자: {}, 예상 일자: {}", dayRequest.getDate(), expectedDate);
                throw new InvalidTravelDayException();
            }

            // 입력한 여행 날짜가 여행 일수의 날짜와 일치하는지 확인
            if (dayRequest.getDayNumber() != (i + 1)) {
                log.warn("여행 일수가 예상 일수와 일치하지 않습니다. 여행 일수: {}, 예상 일수: {}", dayRequest.getDayNumber(), i + 1);
                throw new InvalidTravelDayException();
            }
        }
    }
}
