package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "participants")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @CreationTimestamp
    private LocalDateTime joinedAt;

    @Builder
    public Participants(TravelPlans travelPlans, Long userId, Permission permission) {
        this.travelPlans = travelPlans;
        this.userId = userId;
        this.permission = permission;
    }

    public void changeParticipantPermission(Permission permission) {
        this.permission = permission;
    }
}