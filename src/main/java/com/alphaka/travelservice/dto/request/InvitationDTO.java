package com.alphaka.travelservice.dto.request;

import com.alphaka.travelservice.entity.InvitationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvitationDTO {
    private Long travelId;
    private InvitationStatus invitationStatus;
}
