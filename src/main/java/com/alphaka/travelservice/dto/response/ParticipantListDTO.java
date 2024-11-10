package com.alphaka.travelservice.dto.response;

import com.alphaka.travelservice.entity.Permission;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class ParticipantListDTO {
    private Long participantId;
    private Long travelId;
    private Long userId;
    private String nickname;
    private Permission permission;

    @Builder
    public ParticipantListDTO(Long participantId, Long travelId, Long userId, String nickname, Permission permission) {
        this.participantId = participantId;
        this.travelId = travelId;
        this.userId = userId;
        this.nickname = nickname;
        this.permission = permission;
    }
}
