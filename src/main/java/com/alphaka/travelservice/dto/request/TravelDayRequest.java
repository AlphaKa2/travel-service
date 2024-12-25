package com.alphaka.travelservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
public class TravelDayRequest {
    @NotNull
    private int dayNumber;

    @NotNull
    private LocalDate date;

    @Valid
    @NotNull
    private List<TravelScheduleRequest> schedules;
}
