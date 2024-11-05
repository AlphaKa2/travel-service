package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.InvitationDTO;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.dto.response.InvitationListDTO;
import com.alphaka.travelservice.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping("/invitations")
    public ApiResponse<Long> addInvitationByNick(CurrentUser currentUser, @RequestBody ParticipantRequest request) {

        Long response = invitationService.addInvitation(currentUser, request);
        return new ApiResponse<>(response);
    }

    @GetMapping("/invitations")
    public ApiResponse<List<InvitationListDTO>> listInvitationById(CurrentUser currentUser) {

        List<InvitationListDTO> response = invitationService.getInvitation(currentUser);
        return new ApiResponse<>(response);
    }

    @PutMapping("/invitations")
    public ApiResponse<Long> accessInvitationByNick(CurrentUser currentUser, @Valid @RequestBody InvitationDTO request) {

        Long response = invitationService.changeInvitation(currentUser, request);
        return new ApiResponse<>(response);
    }
}
