package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.ParticipantChangeRequest;
import com.alphaka.travelservice.dto.response.ParticipantListDTO;
import com.alphaka.travelservice.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/participants")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PutMapping
    public ApiResponse<Long> accessParticipantByNick(CurrentUser currentUser, @RequestBody ParticipantChangeRequest request) {

        Long response = participantService.changeParticipant(currentUser, request);
        return new ApiResponse<>(response);
    }

    @GetMapping("/{travelId}")
    public ApiResponse<List<ParticipantListDTO>> listParticipant(CurrentUser currentUser, @PathVariable("travelId") Long travelId) {

        List<ParticipantListDTO> response = participantService.getParticipant(currentUser, travelId);
        return new ApiResponse<>(response);
    }

    @DeleteMapping("/{participantId}")
    public ApiResponse<Long> deleteParticipantById(CurrentUser currentUser, @PathVariable("participantId") Long participantId) {

        Long response = participantService.deleteParticipant(currentUser, participantId);
        return new ApiResponse<>(response);
    }
}
