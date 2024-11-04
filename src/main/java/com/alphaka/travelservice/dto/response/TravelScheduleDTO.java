package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.TravelDays;
import com.alphaka.travelservice.entity.TravelPlaces;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelScheduleDTO {
    private Long scheduleId;
    private List<TravelPlacesDTO> places;
    private int scheduleOrder;
}
