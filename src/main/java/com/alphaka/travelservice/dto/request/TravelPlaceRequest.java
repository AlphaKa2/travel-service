package com.alphaka.travelservice.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TravelPlaceRequest {
    @Size(min = 1, max = 50, message = "장소 이름은 1자 이상 50자 이하로 입력해주세요.")
    private String placeName;

    @Size(min = 1, max = 50, message = "주소는 1자 이상 50자 이하로 입력해주세요.")
    private String address;

    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "경도는 숫자와 소수점만 입력 가능합니다.")
    private String longitude;

    @Pattern(regexp = "^-?\\d+(\\.\\d+)?$", message = "위도는 숫자와 소수점만 입력 가능합니다.")
    private String latitude;
}
