package com.alphaka.travelservice.dto.response;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreferenceResponse {
    private Integer recommendation_trip_id;
    private Integer user_id;
    private Integer travel_status_days;
    private Integer style;
    private Integer motive;
    private String means_of_transportation;
    private String travel_companion_status;
    private String age_group;
    private String purposes;
    private String gender;
}
