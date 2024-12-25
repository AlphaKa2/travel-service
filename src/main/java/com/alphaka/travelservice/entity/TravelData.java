package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "travel_datas")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelDataId;

    @Column(name = "travel_id", nullable = false, length = 255)
    private Long travelId;

    @Column(name = "traveler_id", nullable = false, length = 255)
    private Long travelerId;

    @Column(name = "travel_purpose", nullable = false, length = 255)
    private String travelPurpose;

    @Column(name = "mvmn_nm", length = 255)
    private String mvmnNm;

    @Column(name = "age_grp")
    private Integer ageGrp;

    @Column(name = "gender")
    private String gender;

    @Column(name = "travel_styl_1")
    private Integer travelStyl1;

    @Column(name = "travel_motive_1")
    private Integer travelMotive1;

    @Column(name = "travel_status_accompany")
    private String travelStatusAccompany;

    @Column(name = "travel_status_days")
    private Integer travelStatusDays;

    @Column(name = "visit_area_nm", length = 255)
    private String visitAreaNm;

    @Column(name = "road_addr", length = 255)
    private String roadAddr;

    @Column(name = "x_coord")
    private String xCoord;

    @Column(name = "y_coord")
    private String yCoord;

    @Column(name = "travel_status_ymd")
    private String travelStatusYmd;

    @Column(name = "es_loaded")
    private Integer esLoaded;

    @Builder
    public TravelData(Long travelId, Long travelerId, String travelPurpose, String mvmnNm, Integer ageGrp,
                      String gender, Integer travelStyl1, Integer travelMotive1, String travelStatusAccompany,
                      Integer travelStatusDays, String visitAreaNm, String roadAddr, String xCoord, String yCoord,
                      String travelStatusYmd, Integer esLoaded) {
        this.travelId = travelId;
        this.travelerId = travelerId;
        this.travelPurpose = travelPurpose;
        this.mvmnNm = mvmnNm;
        this.ageGrp = ageGrp;
        this.gender = gender;
        this.travelStyl1 = travelStyl1;
        this.travelMotive1 = travelMotive1;
        this.travelStatusAccompany = travelStatusAccompany;
        this.travelStatusDays = travelStatusDays;
        this.visitAreaNm = visitAreaNm;
        this.roadAddr = roadAddr;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.travelStatusYmd = travelStatusYmd;
        this.esLoaded = esLoaded;
    }
}
