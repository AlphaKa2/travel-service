package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.TravelSchedules;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TravelPlacesDTO {
    private Long placeId;

    private String placeName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
