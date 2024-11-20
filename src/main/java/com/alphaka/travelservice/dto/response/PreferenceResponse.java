package com.alphaka.travelservice.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PreferenceResponse {
    private String travelPurpose;
    private String mvmnNm;
    private Integer ageGrp;
    private String gender;
    private Integer travelStyl1;
    private Integer travelMotive1;
    private String travelStatusAccompany;
    private Integer travelStatusDays;
    private String roadAddr;
}
