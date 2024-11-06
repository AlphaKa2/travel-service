package com.alphaka.travelservice.dto.response;

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
    private Long travelId;
    private String title;
    private String description;
    private List<TravelDayDTO> days;
}
