package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

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

    @OneToMany(mappedBy = "travelSchedules", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelPlaces> places;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
