package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.Permission;
import com.alphaka.travelservice.entity.TravelStatus;
import com.alphaka.travelservice.entity.TravelType;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class TravelPlanResponse {
    private Long travelId;
    private String title;
    private String description;
    private TravelType travelType;
    private TravelStatus travelStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TravelDayResponse> days = new ArrayList<>();
    private String lastUpdatedBy;
    private List<String> participants;
    private Permission permission;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    // QueryDSL을 사용하여 조회한 결과를 매핑하기 위한 생성자
    public TravelPlanResponse(Long travelId, String title, String description, TravelType travelType, TravelStatus travelStatus,
                              LocalDate startDate, LocalDate endDate, LocalDateTime createdAt) {
        this.travelId = travelId;
        this.title = title;
        this.description = description;
        this.travelType = travelType;
        this.travelStatus = travelStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdAt = createdAt;
    }
}
