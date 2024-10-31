package com.alphaka.travelservice.controller;

//import com.alphaka.travelservice.client.AiClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.request.TravelCreateRequest;
import com.alphaka.travelservice.dto.response.TravelListDTO;
import com.alphaka.travelservice.dto.response.TravelPlanDTO;
import com.alphaka.travelservice.entity.TravelPlans;
import com.alphaka.travelservice.service.TravelPlansService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TravelController {

    private final TravelPlansService travelPlansService;

//    private final AiClient aiClient;

    @PostMapping("/travels")
    public ApiResponse<Long> createTravel(CurrentUser currentUser,
                                          @Valid @RequestBody List<TravelCreateRequest> request) {
        TravelCreateRequest innerRequest = request.get(0);
        Long response = travelPlansService.createTravelPlan(currentUser, innerRequest);
        return new ApiResponse<>(response);
    }

    @GetMapping("/travels/{travelId}")
    public ApiResponse<TravelPlanDTO> readTravel(CurrentUser currentUser, @PathVariable("travelId") Long travelId) {
        TravelPlanDTO response = travelPlansService.readTravelPlan(currentUser, travelId);
        return new ApiResponse<>(response);
    }

    @GetMapping("/travels")
    public ApiResponse<List<TravelListDTO>> listTravel(CurrentUser currentUser) {
        List<TravelListDTO> response = travelPlansService.listTravelPlan(currentUser);
        return new ApiResponse<>(response);
    }


//    @PutMapping("/travels")
//    public ApiResponse<Long> updateTravelByDTO() {
//        Long response = travelPlanService.createTravelPlan(currentUser, request);
//        return new ApiResponse<>(response);
//    }

    @DeleteMapping("/travels/{travelId}")
    public ApiResponse<Long> deleteTravelById(CurrentUser currentUser, @PathVariable("travelId") Long travelId) {
        Long response = travelPlansService.deleteTravelPlan(currentUser, travelId);
        return new ApiResponse<>(response);
    }

//    @PostMapping("/travels/convert/{recommendation_trip_id}")
//    public ApiResponse<Long> convertTravel(CurrentUser currentUser, @PathVariable("recommendation_trip_id") String recommendation_trip_id) {
//        // Fetch recommendation data from AI service using Feign Client
//        ApiResponse<TravelCreateRequest> recommendationResponse = aiClient.findTravelById(recommendation_trip_id);
//
//        // Ensure that the response contains data
//        if (recommendationResponse == null || recommendationResponse.getData() == null) {
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recommendation plan not found");
//        }
//
//        // Call the service to convert and save the travel plan
//        Long response = travelPlansService.convertTravelPlan(currentUser, recommendationResponse.getData());
//
//        return new ApiResponse<>(response);
//    }

}
