package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "travel_schedules")
public class TravelSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @OneToOne
    @JoinColumn(name = "day_id", referencedColumnName = "dayId")
    private TravelDays travelDays;

    @OneToMany(mappedBy = "travelSchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelPlaces> places;

    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

}
