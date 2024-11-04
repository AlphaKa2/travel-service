package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationListDTO {
    private Long invitationId;
    private String invitationMessage;
    private InvitationStatus invitationStatus;
}
