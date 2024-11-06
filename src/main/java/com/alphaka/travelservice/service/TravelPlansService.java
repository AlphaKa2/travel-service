package com.alphaka.travelservice.service;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.dto.request.*;
import com.alphaka.travelservice.exception.custom.InvalidTravelDayException;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlansService {

    private final TravelPlansRepository travelPlansRepository;

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
        validateTravelDays(request);

        // 동행에 자기 자신 추가

        log.info("여행 계획 배치 삽입 시작");
        return travelPlansRepository.batchInsertTravelPlan(currentUser.getUserId(), request);
    }

    /**
     * 여행 일수 검증
     * @param request - 여행 계획 생성 요청
     */
    private void validateTravelDays(TravelPlanCreateRequest request) {
        log.info("여행 일수 검증 시작");

        LocalDate startDate = request.getStartDate();
        LocalDate endDate = request.getEndDate();

        // 여행 일정에 따른 두 날짜 사이의 여행 일수 계산 (시작일과 종료일 포함)
        long expectedDayCount = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<?> days = request.getDays();

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

//    public TravelReadRequest readTravelPlan(CurrentUser currentUser, Long travelId) {
//
//        //추가적으로 해당 plan이 사용자 것인지 확인 로직 필요
//        //추가적으로 place를 place_order 기준으로 정렬하여 반환하는 로직 필요
//        log.info("계획 생성 조회. 현재 사용자: {}", currentUser.getNickname());
//
//        // QClass 선언
//        QTravelPlans qTravelPlans = QTravelPlans.travelPlans;
//        QTravelDays qTravelDays = QTravelDays.travelDays;
//        QTravelSchedules qTravelSchedules = QTravelSchedules.travelSchedules;
//        QTravelPlaces qTravelPlaces = QTravelPlaces.travelPlaces;
//
//        // 1. 한 번의 쿼리로 TravelPlans, TravelDays, TravelSchedules, TravelPlaces 조인
//        List<Tuple> result = queryFactory
//                .select(qTravelPlans, qTravelDays, qTravelSchedules, qTravelPlaces)
//                .from(qTravelPlans)
//                .leftJoin(qTravelPlans.travelDays, qTravelDays).on(qTravelPlans.travelId.eq(travelId))
//                .leftJoin(qTravelDays.travelSchedules, qTravelSchedules)
//                .leftJoin(qTravelSchedules.places, qTravelPlaces)
//                .where(qTravelPlans.travelId.eq(travelId))
//                .fetch();
//
//        if (result.isEmpty()) {
//            throw new PlanNotFoundException();
//        }
//
//        // 2. TravelPlan 기본 정보 설정
//        TravelPlans travelPlan = result.get(0).get(qTravelPlans);
//
//        // Convert result to list of TravelDayDTO for the request
//        List<TravelDayDTO> days = result.stream()
//                .collect(Collectors.groupingBy(tuple -> tuple.get(qTravelDays)))
//                .entrySet().stream()
//                .map(entry -> {
//                    TravelDays day = entry.getKey();
//
//                    // 3. TravelSchedules와 TravelPlaces를 DTO로 변환
//                    List<TravelScheduleDTO> schedules = entry.getValue().stream()
//                            .filter(tuple -> tuple.get(qTravelSchedules) != null)
//                            .collect(Collectors.groupingBy(tuple -> tuple.get(qTravelSchedules)))
//                            .entrySet().stream()
//                            .map(scheduleEntry -> {
//                                TravelSchedules schedule = scheduleEntry.getKey();
//
//                                // Convert each TravelPlaces to TravelPlacesDTO
//                                List<TravelPlacesDTO> places = scheduleEntry.getValue().stream()
//                                        .filter(placeTuple -> placeTuple.get(qTravelPlaces) != null)
//                                        .map(placeTuple -> {
//                                            TravelPlaces place = placeTuple.get(qTravelPlaces);
//                                            return new TravelPlacesDTO(
//                                                    place.getPlaceId(),
//                                                    place.getPlaceName(),
//                                                    place.getAddress(),
//                                                    place.getPlaceOrder(),
//                                                    place.getLatitude(),
//                                                    place.getLongitude()
//                                            );
//                                        })
//                                        .collect(Collectors.toList());
//
//                                // Create and return a TravelScheduleDTO with associated places
//                                return new TravelScheduleDTO(
//                                        schedule.getScheduleId(),
//                                        places
//                                );
//                            })
//                            .collect(Collectors.toList());
//
//                    // Create and return a TravelDayDTO entry
//                    return new TravelDayDTO(
//                            day.getDayId(),
//                            String.valueOf(day.getDayNumber()),
//                            schedules
//                    );
//                })
//                .collect(Collectors.toList());
//
//        // 4. TravelUpdateRequest 반환
//        return new TravelReadRequest(
//                travelPlan.getTravelId(),
//                travelPlan.getUserId(),
//                travelPlan.getName(),
//                travelPlan.getDescription(),
//                days,
//                travelPlan.getTravelType(),
//                travelPlan.getTravelStatus(),
//                travelPlan.getStartDate(),
//                travelPlan.getEndDate()
//        );
//    }
//
//
//    public Long convertTravelPlan(CurrentUser currentUser, TravelPlanCreateRequest recommendationData) {
//        // Step 1: Create and save the TravelPlans entry
//        TravelPlans travelPlans = new TravelPlans();
//        travelPlans.setUserId(currentUser.getUserId());
//        travelPlans.setName(recommendationData.getTitle());
//        travelPlans.setDescription(recommendationData.getDescription());
//        travelPlans.setTravelType(TravelType.USER_GENERATED);  // Set an appropriate travel type
//        travelPlans.setTravelStatus(TravelStatus.RECOMMENDED);
//        travelPlans.setCreatedAt(LocalDateTime.now());
//        travelPlans = travelPlansRepository.save(travelPlans);
//
//
//        for (TravelDayRequest dayData : recommendationData.getDays()) {
//            TravelDays travelDays = new TravelDays();
//            travelDays.setTravelPlans(travelPlans);  // Associate with TravelPlans
//            travelDays.setDayNumber(Integer.parseInt(dayData.getDay()));
//            travelDays.setDate(LocalDate.now().plusDays(travelDays.getDayNumber()));
//            travelDays = travelDaysRepository.save(travelDays);
//
//            // Step 3: Create TravelSchedules for each day and process associated places
//            TravelSchedules travelSchedules = new TravelSchedules();
//            travelSchedules.setTravelDays(travelDays);  // Associate with TravelDays
//            travelSchedules.setCreatedAt(LocalDateTime.now());
//            travelSchedulesRepository.save(travelSchedules);
//
//            int placeOrder = 1;
//
//            // Step 4: Add TravelPlaces for each schedule entry
//            for (TravelScheduleRequest scheduleData : dayData.getSchedule()) {
//                TravelPlaces travelPlace = new TravelPlaces();
//                travelPlace.setTravelSchedule(travelSchedules);  // Associate with TravelSchedules
//                travelPlace.setPlaceName(scheduleData.getPlace());
//                travelPlace.setAddress(scheduleData.getAddress());
//                travelPlace.setPlaceOrder(placeOrder++);
//                travelPlace.setLatitude(new BigDecimal(scheduleData.getLatitude()));
//                travelPlace.setLongitude(new BigDecimal(scheduleData.getLongitude()));
//                travelPlace.setCreatedAt(LocalDateTime.now());
//                travelPlacesRepository.save(travelPlace);
//            }
//        }
//
//        // Return the ID of the saved travel plan
//        return travelPlans.getTravelId();
//    }
//
//    @Transactional
//    public Long updateTravelPlan(CurrentUser currentUser, TravelUpdateRequest request) {
//        log.info("계획 업데이트 시작. 현재 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), request.getTravelId());
//
//        // Step 1: Fetch existing TravelPlans entry or throw an exception if not found
//        TravelPlans travelPlans = travelPlansRepository.findById(request.getTravelId())
//                .orElseThrow(() -> new PlanNotFoundException());
//
//        // Step 2: Update TravelPlans basic information
//        travelPlans.setName(request.getTitle());
//        travelPlans.setDescription(request.getDescription());
//        travelPlans.setTravelType(request.getTravelType());
//        travelPlans.setTravelStatus(request.getTravelStatus());
//        travelPlans.setUpdatedAt(LocalDateTime.now());
//
//        // Step 3: Delete all existing TravelDays, TravelSchedules, and TravelPlaces for this plan
//        List<TravelDays> existingDays = travelPlans.getTravelDays();
//        for (TravelDays day : existingDays) {
//            if (day.getTravelSchedules() != null) {
//                travelSchedulesRepository.deleteById(day.getTravelSchedules().getScheduleId());
//            }
//        }
//        travelDaysRepository.deleteAll(existingDays);
//
//        // Step 4: Process new data from the request and save updated entries
//        List<TravelDays> updatedDays = new ArrayList<>();
//
//        for (TravelDayRequest dayData : request.getDays()) {
//            log.info("Processing day {}", dayData.getDay());
//
//            TravelDays travelDays = new TravelDays();
//            travelDays.setTravelPlans(travelPlans);
//            travelDays.setDayNumber(Integer.parseInt(dayData.getDay()));
//            travelDays.setDate(LocalDate.now().plusDays(travelDays.getDayNumber()));
//            travelDays = travelDaysRepository.save(travelDays);
//
//            // Initialize TravelSchedules with a non-null places list
//            TravelSchedules travelSchedules = new TravelSchedules();
//            travelSchedules.setPlaces(new ArrayList<>()); // Ensure places is not null
//            travelSchedules.setTravelDays(travelDays);
//            travelSchedules.setCreatedAt(LocalDateTime.now());
//            travelSchedules = travelSchedulesRepository.save(travelSchedules);
//
//            int placeOrder = 1;
//
//            List<TravelPlaces> updatedPlaces = new ArrayList<>();
//            for (TravelScheduleRequest scheduleData : dayData.getSchedule()) {
//                log.info("Adding place {}", scheduleData.getPlace());
//
//                TravelPlaces travelPlace = new TravelPlaces();
//                travelPlace.setTravelSchedule(travelSchedules);
//                travelPlace.setPlaceName(scheduleData.getPlace());
//                travelPlace.setAddress(scheduleData.getAddress());
//                travelPlace.setPlaceOrder(placeOrder++);
//                travelPlace.setLatitude(new BigDecimal(scheduleData.getLatitude()));
//                travelPlace.setLongitude(new BigDecimal(scheduleData.getLongitude()));
//                travelPlace.setCreatedAt(LocalDateTime.now());
//                updatedPlaces.add(travelPlacesRepository.save(travelPlace));
//            }
//
//            // Add places to the travelSchedules entity
//            travelSchedules.getPlaces().addAll(updatedPlaces);
//
//            // Set the schedule in the day entity and add the day to updatedDays
//            travelDays.setTravelSchedules(travelSchedules);
//            updatedDays.add(travelDays);
//        }
//
//        // Update travel plan with the new days list
//        travelPlans.getTravelDays().clear();
//        travelPlans.getTravelDays().addAll(updatedDays);
//
//        // Save the updated TravelPlans
//        travelPlansRepository.save(travelPlans);
//
//        log.info("계획 업데이트 완료. 여행 계획 ID: {}", travelPlans.getTravelId());
//        return travelPlans.getTravelId();
//    }
//
//
//
//    @Transactional
//    public Long deleteTravelPlan(CurrentUser currentUser, Long travelId) {
//        log.info("계획 삭제 요청. 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);
//
//        // 1. Fetch the travel plan by ID
//        TravelPlans travelPlan = travelPlansRepository.findById(travelId)
//                .orElseThrow(() -> new PlanNotFoundException());
//
//        // 2. Check if the current user has authorization to delete this plan
//        if (!travelPlan.getUserId().equals(currentUser.getUserId())) {
//            throw new UnauthorizedException();
//        }
//
//        // 3. Perform the deletion (Cascade type should be configured in entities)
//        travelPlansRepository.delete(travelPlan);
//
//        log.info("여행 계획 삭제 완료: {}", travelId);
//
//        // 4. Return the ID of the deleted plan
//        return travelId;
//    }
//
//    public List<TravelListDTO> listTravelPlan(CurrentUser currentUser) {
//        // Fetch travel plans created by the current user
//        List<TravelPlans> travelPlans = travelPlansRepository.findByUserId(currentUser.getUserId());
//
//        // Map TravelPlans to TravelListDTO
//        List<TravelListDTO> travelListDTOs = travelPlans.stream()
//                .map(plan -> new TravelListDTO(plan.getTravelId(), plan.getName(), plan.getDescription()))
//                .collect(Collectors.toList());
//
//        return travelListDTOs;
//    }
}
