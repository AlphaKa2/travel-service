package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.InvitationStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class InvitationListDTO {
    private Long travelId;
    private Long invitationId;
    private String invitationMessage;
    private InvitationStatus invitationStatus;

    private LocalDate startDate;
    private LocalDate endDate;
    private String invitedNick;

    @Builder
    public InvitationListDTO(Long travelId, Long invitationId, String invitationMessage, InvitationStatus invitationStatus,
                             LocalDate startDate, LocalDate endDate, String invitedNick) {
        this.travelId = travelId;
        this.invitationId = invitationId;
        this.invitationMessage = invitationMessage;
        this.invitationStatus = invitationStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.invitedNick = invitedNick;
    }
}
