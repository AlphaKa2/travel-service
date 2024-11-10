package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.InvitationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class InvitationListDTO {
    private Long travelId;
    private Long invitationId;
    private String invitationMessage;
    private InvitationStatus invitationStatus;

    @Builder
    public InvitationListDTO(Long travelId, Long invitationId, String invitationMessage, InvitationStatus invitationStatus) {
        this.travelId = travelId;
        this.invitationId = invitationId;
        this.invitationMessage = invitationMessage;
        this.invitationStatus = invitationStatus;
    }
}
