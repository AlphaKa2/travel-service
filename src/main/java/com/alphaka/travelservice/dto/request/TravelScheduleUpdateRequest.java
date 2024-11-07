package com.alphaka.travelservice.dto.request;

import com.alphaka.travelservice.annotation.ValidTimeRange;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidTimeRange
public class TravelScheduleUpdateRequest {

    private Long travelScheduleId;

    @NotNull
    private int order;

    @Valid
    @NotNull
    private TravelPlaceUpdateRequest place;

    private LocalTime startTime;
    private LocalTime endTime;
}
