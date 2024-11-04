package com.alphaka.travelservice.service;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.dto.request.*;
import com.alphaka.travelservice.dto.response.*;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.exception.custom.UnauthorizedException;
import com.alphaka.travelservice.repository.*;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private final JdbcTemplate jdbcTemplate;
    private final TravelPlansRepository travelPlansRepository;

    private final TravelDaysRepository travelDaysRepository;
    private final TravelSchedulesRepository travelSchedulesRepository;

    private final TravelPlacesRepository travelPlacesRepository;
    private final ParticipantsRepository participantsRepository;

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
        travelPlans.setTravelType(TravelType.USER_GENERATED);  // Set an appropriate travel type
        travelPlans.setTravelStatus(TravelStatus.RECOMMENDED);
        travelPlans.setCreatedAt(LocalDateTime.now());
        travelPlans = travelPlansRepository.save(travelPlans);

        // Step 1.5: Create and save the Participants entry with the created TravelPlans
        Participants participant = new Participants();
        participant.setTravelPlans(travelPlans); // Set the reference to the created TravelPlans
        participant.setUserId(currentUser.getUserId());
        participant.setPermission(Permission.VIEW); // Set as OWNER or another default permission
        participant.setJoinedAt(LocalDateTime.now());
        participantsRepository.save(participant);

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






//    @Transactional
//    public Long createTravelPlan(CurrentUser currentUser, TravelCreateRequest request) {
//        log.info("계획 생성 시작. 현재 사용자: {}", currentUser.getNickname());
//
//        // Step 1: Create and save the main TravelPlans entry
//        TravelPlans travelPlans = new TravelPlans();
//        travelPlans.setUserId(currentUser.getUserId());
//        travelPlans.setName(request.getTitle());
//        travelPlans.setDescription(request.getDescription());
//        travelPlans.setStartDate(request.getStartDate());
//        travelPlans.setEndDate(request.getEndDate());
//        travelPlans.setTravelType(TravelType.USER_GENERATED);
//        travelPlans.setTravelStatus(TravelStatus.RECOMMENDED);
//        travelPlans.setCreatedAt(LocalDateTime.now());
//        travelPlans = travelPlansRepository.save(travelPlans); // Save TravelPlans immediately
//
//        // Prepare lists for batch inserts
//        List<TravelDays> travelDaysList = new ArrayList<>();
//        List<TravelSchedules> travelSchedulesList = new ArrayList<>();
//        List<TravelPlaces> travelPlacesList = new ArrayList<>();
//
//        int scheduleOrder = 1; // Initialize schedule order counter
//
//        // Step 2: Process each day and its associated schedules
//        for (DayJsonData dayData : request.getDays()) {
//            // Create TravelDays instance and associate it with TravelPlans
//            TravelDays travelDays = new TravelDays();
//            travelDays.setTravelPlans(travelPlans);
//            travelDays.setDayNumber(Integer.parseInt(dayData.getDay()));
//            travelDays.setDate(LocalDate.now().plusDays(travelDays.getDayNumber()));
//            travelDaysList.add(travelDays); // Add to list for batch save
//
//            // Step 3: Prepare TravelSchedules and TravelPlaces for each day
//            TravelSchedules travelSchedules = new TravelSchedules();
//            travelSchedules.setTravelDays(travelDays); // Associate with TravelDays
//            travelSchedules.setScheduleOrder(scheduleOrder++); // Increment order
//            travelSchedules.setCreatedAt(LocalDateTime.now());
//            travelSchedulesList.add(travelSchedules); // Add to list for batch save
//
//            // Step 4: Add TravelPlaces for each schedule entry
//            for (ScheduleJsonData scheduleData : dayData.getSchedule()) {
//                TravelPlaces travelPlace = new TravelPlaces();
//                travelPlace.setTravelSchedule(travelSchedules); // Associate with TravelSchedules
//                travelPlace.setPlaceName(scheduleData.getPlace());
//                travelPlace.setAddress(scheduleData.getAddress());
//                travelPlace.setLatitude(new BigDecimal(scheduleData.getLatitude()));
//                travelPlace.setLongitude(new BigDecimal(scheduleData.getLongitude()));
//                travelPlace.setCreatedAt(LocalDateTime.now());
//                travelPlacesList.add(travelPlace); // Add to list for batch save
//            }
//        }
//
//        // Batch save all entries to reduce the number of queries
//        travelDaysRepository.saveAll(travelDaysList);
//        travelSchedulesRepository.saveAll(travelSchedulesList);
//        travelPlacesRepository.saveAll(travelPlacesList);
//
//        log.info("계획 생성 완료. 여행 계획 ID: {}", travelPlans.getTravelId());
//        return travelPlans.getTravelId(); // Return the created TravelPlans ID
//    }


    public TravelReadRequest readTravelPlan(CurrentUser currentUser, Long travelId) {
        log.info("계획 생성 조회. 현재 사용자: {}", currentUser.getNickname());

        // QClass 선언
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

        // Convert result to list of TravelDayDTO for the request
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

                                // Convert each TravelPlaces to TravelPlacesDTO
                                List<TravelPlacesDTO> places = scheduleEntry.getValue().stream()
                                        .filter(placeTuple -> placeTuple.get(qTravelPlaces) != null)
                                        .map(placeTuple -> {
                                            TravelPlaces place = placeTuple.get(qTravelPlaces);
                                            return new TravelPlacesDTO(
                                                    place.getPlaceId(),
                                                    place.getPlaceName(),
                                                    place.getAddress(),
                                                    place.getLatitude(),
                                                    place.getLongitude()
                                            );
                                        })
                                        .collect(Collectors.toList());

                                // Create and return a TravelScheduleDTO with associated places
                                return new TravelScheduleDTO(
                                        schedule.getScheduleId(),
                                        places,
                                        schedule.getScheduleOrder()
                                );
                            })
                            .collect(Collectors.toList());

                    // Create and return a TravelDayDTO entry
                    return new TravelDayDTO(
                            day.getDayId(),
                            String.valueOf(day.getDayNumber()),
                            schedules
                    );
                })
                .collect(Collectors.toList());

        // 4. TravelUpdateRequest 반환
        return new TravelReadRequest(
                travelPlan.getTravelId(),
                travelPlan.getUserId(),
                travelPlan.getName(),
                travelPlan.getDescription(),
                days,
                travelPlan.getTravelType(),
                travelPlan.getTravelStatus(),
                travelPlan.getStartDate(),
                travelPlan.getEndDate()
        );
    }


    public Long convertTravelPlan(CurrentUser currentUser, TravelCreateRequest recommendationData) {
        // Step 1: Create and save the TravelPlans entry
        TravelPlans travelPlans = new TravelPlans();
        travelPlans.setUserId(currentUser.getUserId());
        travelPlans.setName(recommendationData.getTitle());
        travelPlans.setDescription(recommendationData.getDescription());
        travelPlans.setTravelType(TravelType.USER_GENERATED);  // Set an appropriate travel type
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
    public Long updateTravelPlan(CurrentUser currentUser, TravelUpdateRequest request) {
        log.info("계획 업데이트 시작. 현재 사용자: {}, 여행 계획 ID: {}", currentUser.getNickname(), request.getTravelId());

        // Step 1: Fetch existing TravelPlans entry or throw an exception if not found
        TravelPlans travelPlans = travelPlansRepository.findById(request.getTravelId())
                .orElseThrow(() -> new PlanNotFoundException());

        // Step 2: Update TravelPlans basic information
        travelPlans.setName(request.getTitle());
        travelPlans.setDescription(request.getDescription());
        travelPlans.setTravelType(request.getTravelType());
        travelPlans.setTravelStatus(request.getTravelStatus());
        travelPlans.setUpdatedAt(LocalDateTime.now());

        // Step 3: Delete all existing TravelDays, TravelSchedules, and TravelPlaces for this plan
        List<TravelDays> existingDays = travelPlans.getTravelDays();
        for (TravelDays day : existingDays) {
            if (day.getTravelSchedules() != null) {
                travelSchedulesRepository.deleteById(day.getTravelSchedules().getScheduleId());
            }
        }
        travelDaysRepository.deleteAll(existingDays);

        // Step 4: Process new data from the request and save updated entries
        List<TravelDays> updatedDays = new ArrayList<>();
        int scheduleOrder = 1;
        for (DayJsonData dayData : request.getDays()) {
            log.info("Processing day {}", dayData.getDay());

            TravelDays travelDays = new TravelDays();
            travelDays.setTravelPlans(travelPlans);
            travelDays.setDayNumber(Integer.parseInt(dayData.getDay()));
            travelDays.setDate(LocalDate.now().plusDays(travelDays.getDayNumber()));
            travelDays = travelDaysRepository.save(travelDays);

            // Initialize TravelSchedules with a non-null places list
            TravelSchedules travelSchedules = new TravelSchedules();
            travelSchedules.setPlaces(new ArrayList<>()); // Ensure places is not null
            travelSchedules.setTravelDays(travelDays);
            travelSchedules.setScheduleOrder(scheduleOrder++);
            travelSchedules.setCreatedAt(LocalDateTime.now());
            travelSchedules = travelSchedulesRepository.save(travelSchedules);

            List<TravelPlaces> updatedPlaces = new ArrayList<>();
            for (ScheduleJsonData scheduleData : dayData.getSchedule()) {
                log.info("Adding place {}", scheduleData.getPlace());

                TravelPlaces travelPlace = new TravelPlaces();
                travelPlace.setTravelSchedule(travelSchedules);
                travelPlace.setPlaceName(scheduleData.getPlace());
                travelPlace.setAddress(scheduleData.getAddress());
                travelPlace.setLatitude(new BigDecimal(scheduleData.getLatitude()));
                travelPlace.setLongitude(new BigDecimal(scheduleData.getLongitude()));
                travelPlace.setCreatedAt(LocalDateTime.now());
                updatedPlaces.add(travelPlacesRepository.save(travelPlace));
            }

            // Add places to the travelSchedules entity
            travelSchedules.getPlaces().addAll(updatedPlaces);

            // Set the schedule in the day entity and add the day to updatedDays
            travelDays.setTravelSchedules(travelSchedules);
            updatedDays.add(travelDays);
        }

        // Update travel plan with the new days list
        travelPlans.getTravelDays().clear();
        travelPlans.getTravelDays().addAll(updatedDays);

        // Save the updated TravelPlans
        travelPlansRepository.save(travelPlans);

        log.info("계획 업데이트 완료. 여행 계획 ID: {}", travelPlans.getTravelId());
        return travelPlans.getTravelId();
    }


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
//        int scheduleOrder = 1;
//
//        for (DayJsonData dayData : request.getDays()) {
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
//            travelSchedules.setScheduleOrder(scheduleOrder++);
//            travelSchedules.setCreatedAt(LocalDateTime.now());
//            travelSchedules = travelSchedulesRepository.save(travelSchedules);
//
//            List<TravelPlaces> updatedPlaces = new ArrayList<>();
//            for (ScheduleJsonData scheduleData : dayData.getSchedule()) {
//                log.info("Adding place {}", scheduleData.getPlace());
//
//                TravelPlaces travelPlace = new TravelPlaces();
//                travelPlace.setTravelSchedule(travelSchedules);
//                travelPlace.setPlaceName(scheduleData.getPlace());
//                travelPlace.setAddress(scheduleData.getAddress());
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
//        log.info("계획 업데이트 완료. 여행 계획 ID: {}", travelPlans.getTravelId());
//
//        return travelPlans.getTravelId();
//    }






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


    private void batchInsertTravelDays(List<TravelDays> travelDaysList) {
        String sql = "INSERT INTO travel_days (travel_id, day_number, date) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, travelDaysList, travelDaysList.size(), (ps, travelDay) -> {
            ps.setLong(1, travelDay.getTravelPlans().getTravelId());
            ps.setInt(2, travelDay.getDayNumber());
            ps.setDate(3, java.sql.Date.valueOf(travelDay.getDate()));
        });
    }

    private void batchInsertTravelSchedules(List<TravelSchedules> travelSchedulesList) {
        String sql = "INSERT INTO travel_schedules (day_id, schedule_order, created_at) VALUES (?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, travelSchedulesList, travelSchedulesList.size(), (ps, travelSchedule) -> {
            ps.setLong(1, travelSchedule.getTravelDays().getDayId());
            ps.setInt(2, travelSchedule.getScheduleOrder());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(travelSchedule.getCreatedAt()));
        });
    }

    private void batchInsertTravelPlaces(List<TravelPlaces> travelPlacesList) {
        String sql = "INSERT INTO travel_places (schedule_id, place_name, address, latitude, longitude, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.batchUpdate(sql, travelPlacesList, travelPlacesList.size(), (ps, travelPlace) -> {
            ps.setLong(1, travelPlace.getTravelSchedule().getScheduleId());
            ps.setString(2, travelPlace.getPlaceName());
            ps.setString(3, travelPlace.getAddress());
            ps.setBigDecimal(4, travelPlace.getLatitude());
            ps.setBigDecimal(5, travelPlace.getLongitude());
            ps.setTimestamp(6, java.sql.Timestamp.valueOf(travelPlace.getCreatedAt()));
        });
    }
}
