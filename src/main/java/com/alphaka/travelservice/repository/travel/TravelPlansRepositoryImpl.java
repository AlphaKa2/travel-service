package com.alphaka.travelservice.repository.travel;

import com.alphaka.travelservice.dto.request.TravelDayRequest;
import com.alphaka.travelservice.dto.request.TravelPlaceRequest;
import com.alphaka.travelservice.dto.request.TravelPlanCreateRequest;
import com.alphaka.travelservice.dto.request.TravelScheduleRequest;
import com.alphaka.travelservice.dto.response.*;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TravelPlansRepositoryImpl implements TravelPlansRepositoryCustom {

    private final JdbcTemplate jdbcTemplate;
    private final JPAQueryFactory queryFactory;

    /**
     * 여행 계획을 상세 조회
     * @param travelId - 여행 계획 ID
     * @return TravelPlanResponse - 여행 계획 상세 정보
     */
    @Override
    public TravelPlanResponse getTravelPlanDetail(Long travelId) {
        QTravelPlans qPlan = QTravelPlans.travelPlans;
        QTravelDays qDay = QTravelDays.travelDays;
        QTravelSchedules qSchedule = QTravelSchedules.travelSchedules;
        QTravelPlaces qPlace = QTravelPlaces.travelPlaces;

        // 여행 계획 조회
        TravelPlanResponse travelPlan = queryFactory
                .select(Projections.constructor(TravelPlanResponse.class,
                        qPlan.travelId,
                        qPlan.travelName,
                        qPlan.description,
                        qPlan.travelType,
                        qPlan.travelStatus,
                        qPlan.startDate,
                        qPlan.endDate,
                        qPlan.createdAt))
                .from(qPlan)
                .where(qPlan.travelId.eq(travelId))
                .fetchOne();

        if (travelPlan == null) throw new PlanNotFoundException();

        // TravelDays와 TravelSchedules, TravelPlaces를 조인하여 한 번에 조회
        List<Tuple> tuples = queryFactory
                .select(qPlan.travelId,
                        qDay.dayId,
                        qDay.dayNumber,
                        qDay.date,
                        qSchedule.scheduleId,
                        qSchedule.scheduleOrder,
                        qSchedule.startTime,
                        qSchedule.endTime,
                        qPlace.placeId,
                        qPlace.placeName,
                        qPlace.address,
                        qPlace.latitude,
                        qPlace.longitude)
                .from(qPlan)
                .leftJoin(qPlan.travelDays, qDay)
                .leftJoin(qDay.travelSchedules, qSchedule)
                .leftJoin(qSchedule.place, qPlace)
                .where(qPlan.travelId.eq(travelId))
                .orderBy(qDay.dayNumber.asc(), qSchedule.scheduleOrder.asc())
                .fetch();

        // Map을 사용하여 TravelDayResponse와 TravelScheduleResponse를 구성
        Map<Long, TravelDayResponse> dayMap = new LinkedHashMap<>();

        for (Tuple tuple : tuples) {
            Long dayId = tuple.get(qDay.dayId);
            if (!dayMap.containsKey(dayId)) {
                TravelDayResponse dayResponse = new TravelDayResponse(
                        dayId,
                        tuple.get(qDay.dayNumber),
                        tuple.get(qDay.date)
                );
                dayMap.put(dayId, dayResponse);
            }

            // 스케줄이 존재할 경우에만 추가
            Long scheduleId = tuple.get(qSchedule.scheduleId);
            if (scheduleId != null) {
                TravelScheduleResponse scheduleResponse = new TravelScheduleResponse(
                        scheduleId,
                        tuple.get(qSchedule.scheduleOrder),
                        new TravelPlaceResponse(
                                tuple.get(qPlace.placeId),
                                tuple.get(qPlace.placeName),
                                tuple.get(qPlace.address),
                                tuple.get(qPlace.latitude),
                                tuple.get(qPlace.longitude)
                        ),
                        tuple.get(qSchedule.startTime),
                        tuple.get(qSchedule.endTime)
                );

                dayMap.get(dayId).getSchedules().add(scheduleResponse);
            }
        }

        // TravelPlanResponse에 TravelDayResponse 목록 설정
        travelPlan.setDays(new ArrayList<>(dayMap.values()));

        return travelPlan;
    }

    /**
     * 여행 계획 목록 조회
     * @param userId  - 사용자 ID
     * @return List<TravelPlanListResponse> - 여행 계획 목록
     */
    @Override
    public List<TravelPlanListResponse> getTravelPlanList(Long userId) {
        QTravelPlans qPlan = QTravelPlans.travelPlans;
        QParticipants qParticipants = QParticipants.participants;

        // 여행 계획 목록 조회
        List<Tuple> tuples = queryFactory
                .select(qPlan.travelId,
                        qPlan.travelName,
                        qPlan.description,
                        qPlan.travelType,
                        qPlan.travelStatus,
                        qPlan.startDate,
                        qPlan.endDate,
                        qPlan.createdAt,
                        qPlan.updatedAt,
                        qParticipants.userId)
                .from(qPlan)
                .leftJoin(qParticipants).on(qPlan.travelId.eq(qParticipants.travelPlans.travelId))
                .orderBy(qPlan.createdAt.desc())
                .fetch();

        // TravelPlanListResponse와 참가자 목록 구성
        Map<Long, TravelPlanListResponse> travelPlanMap = new LinkedHashMap<>();

        for (Tuple tuple : tuples) {
            Long travelId = tuple.get(qPlan.travelId);
            TravelPlanListResponse travelPlan = travelPlanMap.get(travelId);
            if (travelPlan == null) {
                travelPlan = new TravelPlanListResponse(
                        travelId,
                        tuple.get(qPlan.travelName),
                        tuple.get(qPlan.description),
                        tuple.get(qPlan.travelType),
                        tuple.get(qPlan.travelStatus),
                        tuple.get(qPlan.startDate),
                        tuple.get(qPlan.endDate),
                        tuple.get(qPlan.createdAt),
                        tuple.get(qPlan.updatedAt)
                );
                travelPlanMap.put(travelId, travelPlan);
            }

            // 여행 동행자 ID가 존재할 경우에만 추가
            Long participantId = tuple.get(qParticipants.userId);
            if (participantId != null) {
                // 참가자 닉네임은 서비스 계층에서 매핑
                travelPlan.getParticipants().add(participantId.toString()); // 임시로 ID를 문자열로 추가
            }
        }

        return new ArrayList<>(travelPlanMap.values());
    }

    /**
     * 여행 계획과 관련된 모든 데이터를 배치로 삽입
     * @param userId - 여행 계획을 생성하는 사용자의 ID
     * @param request - 여행 계획 생성 요청 객체
     * @return Long - 생성된 여행 계획의 ID
     */
    @Override
    @Transactional
    public Long batchInsertTravelPlan(Long userId, TravelPlanCreateRequest request) {
        log.info("여행 계획 저장 시작");
        Long travelId = insertTravelPlan(userId, request);
        log.info("여행 계획 저장 완료. travelId: {}", travelId);

        log.info("여행 일자 정보 저장 시작");
        List<Long> dayIds = batchInsertTravelDays(travelId, request.getDays());
        log.info("여행 일자 정보 저장 완료. dayIds: {}", dayIds);

        log.info("여행 스케줄 정보 저장 시작");
        List<SchedulePlaceMapping> schedulePlaceMappings = batchInsertTravelSchedules(dayIds, request.getDays());
        log.info("여행 스케줄 정보 저장 완료. schedulePlaceMappings.size(): {}", schedulePlaceMappings.size());

        log.info("여행 장소 정보 저장 시작");
        batchInsertTravelPlaces(schedulePlaceMappings);
        log.info("여행 장소 정보 저장 완료");

        log.info("여행 계획을 저장했습니다. travelId: {}", travelId);
        return travelId;
    }

    /**
     * travel_plans 테이블에 여행 계획을 삽입하고 생성된 travel_id를 반환
     * @param userId - 사용자 ID
     * @param request - 여행 계획 생성 요청 객체
     * @return Long - 생성된 travel_id
     */
    private Long insertTravelPlan(Long userId, TravelPlanCreateRequest request) {
        String sql = "INSERT INTO travel_plans (user_id, travel_name, description, preference_id, travel_type, start_date, " +
                "                               end_date, travel_status, last_updated_by, created_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, userId);
            ps.setString(2, request.getTravelName());
            ps.setString(3, request.getDescription());
            ps.setLong(4, request.getPreferenceId());
            ps.setString(5, request.getTravelType().name());
            ps.setDate(6, Date.valueOf(request.getStartDate()));
            ps.setDate(7, Date.valueOf(request.getEndDate()));
            ps.setString(8, "PLANNED");
            ps.setLong(9, userId);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalStateException("여행 계획을 저장하는데 실패했습니다.");
        }

        return key.longValue();
    }

    /**
     * travel_days 테이블에 여행 일자를 배치로 삽입하고, 생성된 day_id 목록을 반환
     * @param travelId - 여행 계획 ID
     * @param days - 여행 일자 요청 목록
     * @return List<Long> - 생성된 day_id 목록
     */
    private List<Long> batchInsertTravelDays(Long travelId, List<TravelDayRequest> days) {
        String sql = "INSERT INTO travel_days (travel_id, day_number, date) VALUES (?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();

        for (TravelDayRequest dayRequest : days) {
            batchArgs.add(new Object[]{
                    travelId,
                    dayRequest.getDayNumber(),
                    Date.valueOf(dayRequest.getDate())
            });
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);

        // travel_id로 삽입된 day_id들을 조회
        String selectSql = "SELECT day_id FROM travel_days WHERE travel_id = ? ORDER BY day_number ASC";
        List<Long> dayIds = jdbcTemplate.queryForList(selectSql, Long.class, travelId);

        return dayIds;
    }

    /**
     * travel_schedules 테이블에 여행 스케줄을 배치로 삽입하고, 스케줄과 장소 매핑 정보를 반환
     * @param dayIds - 여행 일자 ID 목록
     * @param days - 여행 일자 요청 목록
     * @return List<SchedulePlaceMapping> - 스케줄과 장소 매핑 정보 목록
     */
    private List<SchedulePlaceMapping> batchInsertTravelSchedules(List<Long> dayIds, List<TravelDayRequest> days) {
        String sql = "INSERT INTO travel_schedules (day_id, schedule_order, start_time, end_time, created_at) " +
                     "VALUES (?, ?, ?, ?, NOW())";
        List<Object[]> batchArgs = new ArrayList<>();
        List<SchedulePlaceMapping> schedulePlaceMappings = new ArrayList<>();

        for (int i = 0; i < dayIds.size(); i++) {
            Long dayId = dayIds.get(i);
            TravelDayRequest dayRequest = days.get(i);

            for (TravelScheduleRequest scheduleRequest : dayRequest.getSchedules()) {
                batchArgs.add(new Object[]{
                        dayId,
                        scheduleRequest.getOrder(),
                        Time.valueOf(scheduleRequest.getStartTime()),
                        Time.valueOf(scheduleRequest.getEndTime())
                });

                if (scheduleRequest.getPlace() != null) {
                    schedulePlaceMappings.add(new SchedulePlaceMapping(dayId, scheduleRequest.getPlace()));
                }
            }
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);

        return schedulePlaceMappings;
    }

    /**
     * travel_places 테이블에 여행 장소를 배치로 삽입
     * @param schedulePlaceMappings - 스케줄과 장소 매핑 정보 목록
     */
    private void batchInsertTravelPlaces(List<SchedulePlaceMapping> schedulePlaceMappings) {
        if (schedulePlaceMappings.isEmpty()) {
            log.info("삽입할 여행 장소가 없습니다.");
            return;
        }

        String sql = "INSERT INTO travel_places (schedule_id, place_name, address, longitude, latitude) " +
                     "VALUES (?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        final int batchSize = 50;

        // 모든 schedule_id를 한 번에 조회
        List<Long> scheduleIds = fetchScheduleIds(schedulePlaceMappings);

        for (int i = 0; i < schedulePlaceMappings.size(); i++) {
            Long scheduleId = scheduleIds.get(i);
            TravelPlaceRequest place = schedulePlaceMappings.get(i).getPlaceRequest();

            batchArgs.add(new Object[]{
                    scheduleId,
                    place.getPlaceName(),
                    place.getAddress(),
                    place.getLongitude(),
                    place.getLatitude()
            });

            // 배치 크기마다 삽입을 수행
            if (batchArgs.size() == batchSize) {
                jdbcTemplate.batchUpdate(sql, batchArgs);
                batchArgs.clear();
            }
        }

        // 남은 데이터 삽입
        if (!batchArgs.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, batchArgs);
        }

        log.info("여행 장소 정보 배치 삽입 완료");
    }

    /**
     * 스케줄과 장소 매핑 정보를 기반으로 모든 schedule_id를 한 번에 조회
     * @param schedulePlaceMappings - 스케줄과 장소 매핑 정보 목록
     * @return List<Long> - 조회된 schedule_id 목록
     */
    private List<Long> fetchScheduleIds(List<SchedulePlaceMapping> schedulePlaceMappings) {
        if (schedulePlaceMappings.isEmpty()) {
            return new ArrayList<>();
        }

        // 모든 day_id를 중복 없이 추출
        List<Long> uniqueDayIds = schedulePlaceMappings.stream()
                .map(SchedulePlaceMapping::getDayId)
                .distinct()
                .toList();

        // day_id에 대한 모든 schedule_id를 조회
        String placeholders = String.join(",", uniqueDayIds.stream().map(id -> "?").toArray(String[]::new));
        String sql = "SELECT schedule_id FROM travel_schedules WHERE day_id IN (" + placeholders + ") ORDER BY day_id ASC, schedule_order ASC";

        List<Object> params = new ArrayList<>(uniqueDayIds);

        List<Long> scheduleIds = jdbcTemplate.queryForList(sql, Long.class, params.toArray());

        return scheduleIds;
    }

    /**
     * 스케줄과 장소 매핑 정보
     */
    @Getter
    @AllArgsConstructor
    private static class SchedulePlaceMapping {
        private final Long dayId;
        private final TravelPlaceRequest placeRequest;
    }

    /**
     * 여행 계획 목록 조회 (사용자가 참가자로 포함된 계획만 조회)
     * @param userId - 사용자 ID
     * @return List<TravelPlanListResponse> - 여행 계획 목록
     */
    @Override
    public List<TravelPlanListResponse> getTravelPlanListIncludingParticipant(Long userId) {
        QTravelPlans qPlan = QTravelPlans.travelPlans;
        QParticipants qParticipants = QParticipants.participants;

        // 여행 계획 목록 조회 (사용자가 참가자로 포함된 계획만)
        List<Tuple> tuples = queryFactory
                .select(qPlan.travelId,
                        qPlan.travelName,
                        qPlan.description,
                        qPlan.travelType,
                        qPlan.travelStatus,
                        qPlan.startDate,
                        qPlan.endDate,
                        qPlan.createdAt,
                        qPlan.updatedAt,
                        qParticipants.userId)
                .from(qPlan)
                .join(qParticipants).on(qPlan.travelId.eq(qParticipants.travelPlans.travelId))
                .where(qParticipants.userId.eq(userId)) // 사용자가 참가자로 포함된 계획만 필터링
                .orderBy(qPlan.createdAt.desc())
                .fetch();

        // TravelPlanListResponse와 참가자 목록 구성
        Map<Long, TravelPlanListResponse> travelPlanMap = new LinkedHashMap<>();
        for (Tuple tuple : tuples) {
            Long travelId = tuple.get(qPlan.travelId);
            TravelPlanListResponse travelPlan = travelPlanMap.get(travelId);

            if (travelPlan == null) {
                travelPlan = new TravelPlanListResponse(
                        travelId,
                        tuple.get(qPlan.travelName),
                        tuple.get(qPlan.description),
                        tuple.get(qPlan.travelType),
                        tuple.get(qPlan.travelStatus),
                        tuple.get(qPlan.startDate),
                        tuple.get(qPlan.endDate),
                        tuple.get(qPlan.createdAt),
                        tuple.get(qPlan.updatedAt)
                );
                travelPlanMap.put(travelId, travelPlan);
            }

            // 여행 동행자 ID가 존재할 경우에만 추가
            Long participantId = tuple.get(qParticipants.userId);
            if (participantId != null) {
                // 참가자 닉네임은 서비스 계층에서 매핑
                travelPlan.getParticipants().add(participantId.toString()); // 임시로 ID를 문자열로 추가
            }
        }

        return new ArrayList<>(travelPlanMap.values());
    }

}
