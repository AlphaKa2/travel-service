package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "travel_plans")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelPlans {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long travelId;

    private Long userId;
    private String travelName;
    private String description;
    private Long preferenceId;

    @Enumerated(EnumType.STRING)
    private TravelType travelType;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TravelStatus travelStatus = TravelStatus.PLANNED;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private Long lastUpdatedBy;

    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "travelPlans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelDays> travelDays = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participants> participants = new ArrayList<>();

    @OneToMany(mappedBy = "travelPlans", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Invitations> invitations = new ArrayList<>();

    // 여행 계획을 업데이트
    public void updateTravelPlans(String travelName, String description, LocalDate startDate, LocalDate endDate, Long lastUpdatedBy) {
        this.travelName = travelName;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.lastUpdatedBy = lastUpdatedBy;
    }

    // 여행 계획의 상태를 변경
    public void changeTravelStatus(TravelStatus travelStatus) {
        this.travelStatus = travelStatus;
    }
}
