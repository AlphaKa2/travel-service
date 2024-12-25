package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.InvitationDTO;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.dto.response.InvitationListDTO;
import com.alphaka.travelservice.dto.response.InvitedListDTO;
import com.alphaka.travelservice.service.InvitationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invitations")
@RequiredArgsConstructor
public class InvitationController {

    private final InvitationService invitationService;

    @PostMapping()
    public ApiResponse<Long> addInvitationByNick(CurrentUser currentUser, @RequestBody ParticipantRequest request) {

        Long response = invitationService.addInvitation(currentUser, request);
        return new ApiResponse<>(response);
    }

    @GetMapping
    public ApiResponse<List<InvitationListDTO>> listInvitationById(CurrentUser currentUser) {

        List<InvitationListDTO> response = invitationService.getInvitation(currentUser);
        return new ApiResponse<>(response);
    }

    @PutMapping
    public ApiResponse<Long> accessInvitationByNick(CurrentUser currentUser, @Valid @RequestBody InvitationDTO request) {

        Long response = invitationService.changeInvitation(currentUser, request);
        return new ApiResponse<>(response);
    }

    @GetMapping("/{travelId}")
    public ApiResponse<List<InvitedListDTO>> listInvitedByTravelId(CurrentUser currentUser, @PathVariable("travelId") Long travelId) {

        List<InvitedListDTO> response = invitationService.getInvited(currentUser, travelId);
        return new ApiResponse<>(response);
    }

    @DeleteMapping("/{invitationId}")
    public ApiResponse<Long> deleteInvitationById(CurrentUser currentUser, @PathVariable Long invitationId) {

        Long response = invitationService.deleteInvitation(currentUser, invitationId);
        return new ApiResponse<>(response);
    }

}
