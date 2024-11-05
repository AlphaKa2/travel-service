package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantListDTO {
    private Long participantId;
    private Long travelId;
    private Long userId;
    private String nickname;
    private Permission permission;
}
