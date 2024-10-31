package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "travel_plans")
public class TravelPlans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelId;

    private Long userId;
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private TravelType travelType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TravelStatus travelStatus = TravelStatus.RECOMMENDED;

    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    @Column(nullable = true)
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "travelPlans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelDays> travelDays;

    // Add other getters and setters
}
