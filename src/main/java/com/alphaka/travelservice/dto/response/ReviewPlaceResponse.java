package com.alphaka.travelservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewPlaceResponse {
    private Long placeId;
    private String placeName;
    private String placeAddress;
    private String latitude;
    private String longitude;

    //Querydsl을 사용하여 조회한 결과를 매핑하기 위한 생성자
    public ReviewPlaceResponse(Long placeId, String placeName, String placeAddress, String latitude, String longitude) {
        this.placeId = placeId;
        this.placeName = placeName;
        this.placeAddress = placeAddress;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
