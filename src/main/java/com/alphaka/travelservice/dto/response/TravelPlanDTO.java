package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.dto.request.DayJsonData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlanDTO {
    private String title;
    private String description;
    private List<TravelDayDTO> days;
}
