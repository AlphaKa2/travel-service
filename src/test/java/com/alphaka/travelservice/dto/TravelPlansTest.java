package com.alphaka.travelservice.dto;

import com.alphaka.travelservice.TestUtil;
import com.alphaka.travelservice.entity.TravelPlans;
import com.alphaka.travelservice.entity.TravelStatus;
import com.alphaka.travelservice.entity.TravelType;

import java.time.LocalDate;
import java.util.ArrayList;

public class TravelPlansTest extends TravelPlans {


    public TravelPlansTest(Long travelId, String travelName, Long userId) {
        // TravelPlans 엔티티의 필드를 초기화
        TestUtil.setField(this, "travelId", travelId);
        TestUtil.setField(this, "userId", userId);
        TestUtil.setField(this, "travelName", travelName);
        TestUtil.setField(this, "description", "This is a test travel plan.");
        TestUtil.setField(this, "preferenceId", 1L);
        TestUtil.setField(this, "travelType", TravelType.USER_GENERATED); // 예시 타입
        TestUtil.setField(this, "startDate", LocalDate.of(2024, 1, 1));
        TestUtil.setField(this, "endDate", LocalDate.of(2024, 1, 10));
        TestUtil.setField(this, "travelStatus", TravelStatus.PLANNED);
        TestUtil.setField(this, "createdAt", null); // CreationTimestamp는 자동으로 처리
        TestUtil.setField(this, "updatedAt", null); // UpdateTimestamp도 자동으로 처리
        TestUtil.setField(this, "lastUpdatedBy", userId);
        TestUtil.setField(this, "deletedAt", null);
        TestUtil.setField(this, "travelDays", new ArrayList<>()); // 빈 리스트
        TestUtil.setField(this, "participants", new ArrayList<>()); // 빈 리스트
        TestUtil.setField(this, "invitations", new ArrayList<>()); // 빈 리스트
    }

    public TravelPlansTest(Long travelId, String travelName, Long userId, TravelType travelType, TravelStatus travelStatus) {
        this(travelId, travelName, userId);
        TestUtil.setField(this, "travelType", travelType);
        TestUtil.setField(this, "travelStatus", travelStatus);
    }
}