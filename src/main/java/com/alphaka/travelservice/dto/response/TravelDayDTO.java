package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.dto.request.ScheduleJsonData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelDayDTO {
    private Long dayId;
    private String day;
    private List<TravelScheduleDTO> schedule;
}
