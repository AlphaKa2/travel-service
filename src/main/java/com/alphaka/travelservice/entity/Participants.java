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

    private Long travelId;

    private Long userId2;

    @Enumerated(EnumType.STRING)
    private Permission permission;

    private LocalDateTime joinedAt;

    // Getters and Setters
}