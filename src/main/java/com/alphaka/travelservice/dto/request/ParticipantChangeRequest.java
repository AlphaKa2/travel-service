package com.alphaka.travelservice.dto.request;

import com.alphaka.travelservice.entity.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantChangeRequest {
    private Long participantId;
    private Permission permission;
}
