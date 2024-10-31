package com.alphaka.travelservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelScheduleDTO {
    private String place;
    private String longitude;
    private String latitude;
    private String address;
}
