package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "participants")
public class Participants {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    private TravelPlans travelPlans; // Reference to TravelPlans

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Permission permission = Permission.VIEW;

    private LocalDateTime joinedAt;
}