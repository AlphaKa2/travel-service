package com.alphaka.travelservice.dto.request;

import com.alphaka.travelservice.annotation.ValidDateRange;
import com.alphaka.travelservice.entity.TravelType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidDateRange
public class TravelPlanCreateRequest {

    @Size(min = 1, max = 30, message = "여행 이름은 1자 이상 30자 이하로 입력해주세요.")
    private String travelName;

    @Size(min = 1, max = 100, message = "여행 설명은 1자 이상 100자 이하로 입력해주세요.")
    private String description;

    @NotNull
    private TravelType travelType;

    @NotNull(message = "여행 시작일을 입력해주세요.")
    private LocalDate startDate;

    @NotNull(message = "여행 종료일을 입력해주세요.")
    private LocalDate endDate;

    @Valid
    @NotNull(message = "여행 상세 정보를 입력해주세요.")
    private List<TravelDayRequest> days;
}

