package com.alphaka.travelservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public
class ScheduleJsonData {
    private String place;
    private String longitude;
    private String latitude;
    private String address;
}
