package com.alphaka.travelservice.dto.request;

import com.alphaka.travelservice.entity.TravelStatus;
import com.alphaka.travelservice.entity.TravelType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelUpdateRequest {
    private Long travelId;
    private Long userId;
    private String title;
    private String description;
    private List<TravelDayRequest> days;
    private TravelType travelType;
    private TravelStatus travelStatus;

    private LocalDate startDate;
    private LocalDate endDate;
}
