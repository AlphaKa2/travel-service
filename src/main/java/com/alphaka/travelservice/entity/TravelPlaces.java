package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "travel_places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPlaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private TravelSchedules travelSchedules;

    private String placeName;
    private String address;
    private String latitude;
    private String longitude;

    @Builder
    public TravelPlaces(TravelSchedules travelSchedules, String placeName, String address, String latitude, String longitude) {
        this.travelSchedules = travelSchedules;
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateTravelPlaces(TravelSchedules travelSchedules, String placeName, String address, String latitude, String longitude) {
        this.travelSchedules = travelSchedules;
        this.placeName = placeName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

