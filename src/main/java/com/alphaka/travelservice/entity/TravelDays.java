package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Table(name = "travel_days")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TravelDays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    private TravelPlans travelPlans;

    private int dayNumber;
    private LocalDate date;

    @OneToMany(mappedBy = "travelDays", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TravelSchedules> travelSchedules;

}
