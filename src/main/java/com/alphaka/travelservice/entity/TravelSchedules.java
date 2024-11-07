package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@Table(name = "travel_schedules")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelSchedules {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    private int scheduleOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "day_id")
    private TravelDays travelDays;

    private LocalTime startTime;
    private LocalTime endTime;

    @OneToOne(mappedBy = "travelSchedules", cascade = CascadeType.ALL, orphanRemoval = true)
    private TravelPlaces place;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public TravelSchedules(TravelDays travelDays, int scheduleOrder, LocalTime startTime, LocalTime endTime) {
        this.travelDays = travelDays;
        this.scheduleOrder = scheduleOrder;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void updateTravelSchedules(TravelDays travelDays, int scheduleOrder, LocalTime startTime, LocalTime endTime) {
        this.travelDays = travelDays;
        this.scheduleOrder = scheduleOrder;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void setPlace(TravelPlaces place) {
        this.place = place;
    }
}
