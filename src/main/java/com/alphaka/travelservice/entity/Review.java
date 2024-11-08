package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "reviews")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_plans_id", nullable = false)
    private TravelPlans travelPlans;

    @Column(name = "preference_id", nullable = false)
    private Long preferenceId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewDetail> reviewDetails = new ArrayList<>();

    @Builder
    public Review(TravelPlans travelPlans, Long preferenceId, Long userId) {
        this.travelPlans = travelPlans;
        this.preferenceId = preferenceId;
        this.userId = userId;
    }
}
