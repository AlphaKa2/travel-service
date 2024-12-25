package com.alphaka.travelservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "invitations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitations {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invitationId;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "travel_id")
    private TravelPlans travelPlans;

    private String invitationMessage;

    @Enumerated(EnumType.STRING)
    private InvitationStatus status = InvitationStatus.PENDING;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Builder
    public Invitations (TravelPlans travelPlans, Long userId, String invitationMessage) {
        this.travelPlans = travelPlans;
        this.userId = userId;
        this.invitationMessage = invitationMessage;
        this.status = InvitationStatus.PENDING;
    }

    public void changeInvitationStatus (InvitationStatus status) {
        this.status = status;
    }

}