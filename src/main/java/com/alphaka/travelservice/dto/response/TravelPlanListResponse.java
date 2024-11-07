package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.TravelStatus;
import com.alphaka.travelservice.entity.TravelType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TravelPlanListResponse {
    private Long travelId;
    private String travelName;
    private String description;
    private TravelType type;
    private TravelStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> participants = new ArrayList<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // QueryDSL을 사용하여 조회한 여행 계획 정보를 응답하기 위한 생성자
    public TravelPlanListResponse(Long travelId, String travelName, String description, TravelType type, TravelStatus status,
                                  LocalDate startDate, LocalDate endDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.travelId = travelId;
        this.travelName = travelName;
        this.description = description;
        this.type = type;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
