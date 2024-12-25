package com.alphaka.travelservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
public class TravelScheduleResponse {
    private Long scheduleId;
    private int scheduleOrder;
    private TravelPlaceResponse place;
    private LocalTime startTime;
    private LocalTime endTime;

    // 생성자 추가
    public TravelScheduleResponse(Long scheduleId, int scheduleOrder, TravelPlaceResponse place, LocalTime startTime, LocalTime endTime) {
        this.scheduleId = scheduleId;
        this.scheduleOrder = scheduleOrder;
        this.place = place;
        this.startTime = startTime;
        this.endTime = endTime;
    }
}
