package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "review_details")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ReviewDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    private TravelPlaces travelPlaces;

    @Column(name = "rating", nullable = false)
    @Min(1) @Max(5)
    private int rating;
}
