package com.alphaka.travelservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TravelDayResponse {
    private Long dayId;
    private int dayNumber;
    private LocalDate date;
    private List<TravelScheduleResponse> schedules = new ArrayList<>();

    // QueryDSL을 사용하여 조회한 결과를 매핑하기 위한 생성자
    public TravelDayResponse(Long dayId, int dayNumber, LocalDate date) {
        this.dayId = dayId;
        this.dayNumber = dayNumber;
        this.date = date;
        this.schedules = new ArrayList<>();
    }
}
