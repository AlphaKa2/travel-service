package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Table(name = "travel_places")
public class TravelPlaces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private TravelSchedules travelSchedule;

    private String placeName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
}

