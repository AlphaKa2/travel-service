package com.alphaka.travelservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TravelPlaceResponse {
    private Long placeId;
    private String placeName;
    private String address;
    private String latitude;
    private String longitude;

    // QueryDSL을 사용하여 조회한 결과를 매핑하기 위한 생성자
    public TravelPlaceResponse(Long placeId, String placeName, String address, String latitude, String longitude) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
