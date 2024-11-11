package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.InvitationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class InvitedListDTO {

    private Long travelId;
    private Long invitationId;
    private String invitationMessage;
    private InvitationStatus invitationStatus;
    private String nickname;

    private Long userId;

    @Builder
    public InvitedListDTO(Long travelId, Long invitationId, String invitationMessage, InvitationStatus invitationStatus, String nickname, Long userId) {
        this.travelId = travelId;
        this.invitationId = invitationId;
        this.invitationMessage = invitationMessage;
        this.invitationStatus = invitationStatus;
        this.nickname = nickname;
        this.userId = userId;
    }
}
