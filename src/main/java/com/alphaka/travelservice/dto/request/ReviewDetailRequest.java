package com.alphaka.travelservice.dto.request;

import com.alphaka.travelservice.entity.TravelPlaces;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReviewDetailRequest {

    @NotNull(message = "장소 ID는 필수입니다.")
    private TravelPlaces place;

    @Min(value = 1, message = "평점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5 이하여야 합니다.")
    private int rating;
}
