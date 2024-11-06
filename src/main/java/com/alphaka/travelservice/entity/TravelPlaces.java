package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Getter
@Table(name = "travel_places")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPlaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private TravelSchedules travelSchedules;

    private String placeName;
    private String address;
    private String latitude;
    private String longitude;
}

