package com.alphaka.travelservice.controller;

import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.dto.request.TravelCreateRequest;
import com.alphaka.travelservice.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//@RestController
//@RequiredArgsConstructor
//public class ParticipantController {
//
//    private final ParticipantService participantService;
//
//    @PostMapping("/participants")
//    public ApiResponse<Long> addParticipantByNick(CurrentUser currentUser, ParticipantRequest request) {
//
//        Long response = participantService.addParticipant(currentUser, request);
//        return new ApiResponse<>(response);
//    }
//}
