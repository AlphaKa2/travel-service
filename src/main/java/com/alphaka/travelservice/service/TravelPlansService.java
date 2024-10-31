package com.alphaka.travelservice.service;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.dto.request.DayJsonData;
import com.alphaka.travelservice.dto.request.ScheduleJsonData;
import com.alphaka.travelservice.dto.request.TravelCreateRequest;
import com.alphaka.travelservice.dto.response.TravelDayDTO;
import com.alphaka.travelservice.dto.response.TravelListDTO;
import com.alphaka.travelservice.dto.response.TravelPlanDTO;
import com.alphaka.travelservice.dto.response.TravelScheduleDTO;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.exception.custom.UnauthorizedException;
import com.alphaka.travelservice.repository.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPlansService {
    private final TravelPlansRepository travelPlansRepository;

    private final TravelDaysRepository travelDaysRepository;
    private final TravelSchedulesRepository travelSchedulesRepository;

    private final TravelPlacesRepository travelPlacesRepository;

    private final JPAQueryFactory queryFactory;

    @Transactional
    public Long createTravelPlan(CurrentUser currentUser, TravelCreateRequest request) {
        // Step 1: Create and save TravelPlans
        // Step 1: Create and save the TravelPlans entry
        log.info("계획 생성 시작. 현재 사용자: {}", currentUser.getNickname());
        TravelPlans travelPlans = new TravelPlans();
        travelPlans.setUserId(currentUser.getUserId());
        travelPlans.setName(request.getTitle());
        travelPlans.setDescription(request.getDescription());
        travelPlans.setTravelType(TravelType.LEISURE);  // Set an appropriate travel type
        travelPlans.setTravelStatus(TravelStatus.RECOMMENDED);
        travelPlans.setCreatedAt(LocalDateTime.now());
        travelPlans = travelPlansRepository.save(travelPlans);

        // Step 2: Process each day and its associated schedules
        int scheduleOrder = 1;  // Initialize the schedule order counter

        for (DayJsonData dayData : request.getDays()) {
            TravelDays travelDays = new TravelDays();
            travelDays.setTravelPlans(travelPlans);  // Associate with TravelPlans
            travelDays.setDayNumber(Integer.parseInt(dayData.getDay()));
            travelDays.setDate(LocalDate.now().plusDays(travelDays.getDayNumber()));
            travelDays = travelDaysRepository.save(travelDays);

            // Step 3: Create TravelSchedules for each day and process associated places
            TravelSchedules travelSchedules = new TravelSchedules();
            travelSchedules.setTravelDays(travelDays);  // Associate with TravelDays
            travelSchedules.setScheduleOrder(scheduleOrder++);  // Set and increment schedule order
            travelSchedules.setCreatedAt(LocalDateTime.now());
            travelSchedulesRepository.save(travelSchedules);

            // Step 4: Add TravelPlaces for each schedule entry
            for (ScheduleJsonData scheduleData : dayData.getSchedule()) {
                TravelPlaces travelPlace = new TravelPlaces();
                travelPlace.setTravelSchedule(travelSchedules);  // Associate with TravelSchedules
                travelPlace.setPlaceName(scheduleData.getPlace());
                travelPlace.setAddress(scheduleData.getAddress());
                travelPlace.setLatitude(new BigDecimal(scheduleData.getLatitude()));
                travelPlace.setLongitude(new BigDecimal(scheduleData.getLongitude()));
                travelPlace.setCreatedAt(LocalDateTime.now());
                travelPlacesRepository.save(travelPlace);
            }
        }

        return travelPlans.getTravelId(); // Return the created TravelPlans ID
    }

    public TravelPlanDTO readTravelPlan(CurrentUser currentUser, Long travelId) {
        log.info("계획 생성 조회. 현재 사용자: {}", currentUser.getNickname());
        // Q클래스 선언
        QTravelPlans qTravelPlans = QTravelPlans.travelPlans;
        QTravelDays qTravelDays = QTravelDays.travelDays;
        QTravelSchedules qTravelSchedules = QTravelSchedules.travelSchedules;
        QTravelPlaces qTravelPlaces = QTravelPlaces.travelPlaces;

        // 1. 한 번의 쿼리로 TravelPlans, TravelDays, TravelSchedules, TravelPlaces 조인
        List<Tuple> result = queryFactory
                .select(qTravelPlans, qTravelDays, qTravelSchedules, qTravelPlaces)
                .from(qTravelPlans)
                .leftJoin(qTravelPlans.travelDays, qTravelDays).on(qTravelPlans.travelId.eq(travelId))
                .leftJoin(qTravelDays.travelSchedules, qTravelSchedules)
                .leftJoin(qTravelSchedules.places, qTravelPlaces)
                .where(qTravelPlans.travelId.eq(travelId))
                .fetch();

        if (result.isEmpty()) {
            throw new PlanNotFoundException();
        }

        // 2. TravelPlan 기본 정보 설정
        TravelPlans travelPlan = result.get(0).get(qTravelPlans);
        List<TravelDayDTO> days = result.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(qTravelDays)))
                .entrySet().stream()
                .map(entry -> {
                    TravelDays day = entry.getKey();

                    // 3. TravelSchedules와 TravelPlaces를 DTO로 변환
                    List<TravelScheduleDTO> schedules = entry.getValue().stream()
                            .filter(tuple -> tuple.get(qTravelSchedules) != null)
                            .collect(Collectors.groupingBy(tuple -> tuple.get(qTravelSchedules)))
                            .entrySet().stream()
                            .map(scheduleEntry -> {
                                TravelSchedules schedule = scheduleEntry.getKey();

                                // TravelPlaces를 TravelScheduleDTO로 매핑
                                List<TravelScheduleDTO> scheduleDTOs = scheduleEntry.getValue().stream()
                                        .map(placeTuple -> {
                                            TravelPlaces place = placeTuple.get(qTravelPlaces);
                                            return new TravelScheduleDTO(
                                                    place.getPlaceName(),
                                                    place.getLongitude().toString(),
                                                    place.getLatitude().toString(),
                                                    place.getAddress()
                                            );
                                        }).collect(Collectors.toList());

                                return scheduleDTOs;
                            })
                            .flatMap(List::stream) // 모든 scheduleDTO를 평면화하여 수집
                            .collect(Collectors.toList());

                    return new TravelDayDTO(String.valueOf(day.getDayNumber()), schedules);
                })
                .collect(Collectors.toList());

        // 4. TravelPlanDTO 반환
        return new TravelPlanDTO(travelPlan.getName(), travelPlan.getDescription(), days);
    }

    public Long convertTravelPlan(CurrentUser currentUser, TravelCreateRequest recommendationData) {
        // Step 1: Create and save the TravelPlans entry
        TravelPlans travelPlans = new TravelPlans();
        travelPlans.setUserId(currentUser.getUserId());
        travelPlans.setName(recommendationData.getTitle());
        travelPlans.setDescription(recommendationData.getDescription());
        travelPlans.setTravelType(TravelType.LEISURE);  // Set an appropriate travel type
        travelPlans.setTravelStatus(TravelStatus.RECOMMENDED);
        travelPlans.setCreatedAt(LocalDateTime.now());
        travelPlans = travelPlansRepository.save(travelPlans);

        // Step 2: Process each day and its associated schedules
        int scheduleOrder = 1;  // Initialize the schedule order counter

        for (DayJsonData dayData : recommendationData.getDays()) {
            TravelDays travelDays = new TravelDays();
            travelDays.setTravelPlans(travelPlans);  // Associate with TravelPlans
            travelDays.setDayNumber(Integer.parseInt(dayData.getDay()));
            travelDays.setDate(LocalDate.now().plusDays(travelDays.getDayNumber()));
            travelDays = travelDaysRepository.save(travelDays);

            // Step 3: Create TravelSchedules for each day and process associated places
            TravelSchedules travelSchedules = new TravelSchedules();
            travelSchedules.setTravelDays(travelDays);  // Associate with TravelDays
            travelSchedules.setScheduleOrder(scheduleOrder++);  // Set and increment schedule order
            travelSchedules.setCreatedAt(LocalDateTime.now());
            travelSchedulesRepository.save(travelSchedules);

            // Step 4: Add TravelPlaces for each schedule entry
            for (ScheduleJsonData scheduleData : dayData.getSchedule()) {
                TravelPlaces travelPlace = new TravelPlaces();
                travelPlace.setTravelSchedule(travelSchedules);  // Associate with TravelSchedules
                travelPlace.setPlaceName(scheduleData.getPlace());
                travelPlace.setAddress(scheduleData.getAddress());
                travelPlace.setLatitude(new BigDecimal(scheduleData.getLatitude()));
                travelPlace.setLongitude(new BigDecimal(scheduleData.getLongitude()));
                travelPlace.setCreatedAt(LocalDateTime.now());
                travelPlacesRepository.save(travelPlace);
            }
        }

        // Return the ID of the saved travel plan
        return travelPlans.getTravelId();
    }


    @Transactional
    public Long deleteTravelPlan(CurrentUser currentUser, Long travelId) {
        log.info("계획 삭제 요청. 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), travelId);

        // 1. Fetch the travel plan by ID
        TravelPlans travelPlan = travelPlansRepository.findById(travelId)
                .orElseThrow(() -> new PlanNotFoundException());

        // 2. Check if the current user has authorization to delete this plan
        if (!travelPlan.getUserId().equals(currentUser.getUserId())) {
            throw new UnauthorizedException();
        }

        // 3. Perform the deletion (Cascade type should be configured in entities)
        travelPlansRepository.delete(travelPlan);

        log.info("여행 계획 삭제 완료: {}", travelId);

        // 4. Return the ID of the deleted plan
        return travelId;
    }

    public List<TravelListDTO> listTravelPlan(CurrentUser currentUser) {
        // Fetch travel plans created by the current user
        List<TravelPlans> travelPlans = travelPlansRepository.findByUserId(currentUser.getUserId());

        // Map TravelPlans to TravelListDTO
        List<TravelListDTO> travelListDTOs = travelPlans.stream()
                .map(plan -> new TravelListDTO(plan.getTravelId(), plan.getName(), plan.getDescription()))
                .collect(Collectors.toList());

        return travelListDTOs;
    }
}
