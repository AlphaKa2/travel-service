//package com.alphaka.travelservice.controller;
//
//import com.alphaka.travelservice.common.dto.CurrentUser;
//import com.alphaka.travelservice.common.response.ApiResponse;
//import com.alphaka.travelservice.dto.request.InvitationDTO;
//import com.alphaka.travelservice.dto.request.ParticipantRequest;
//import com.alphaka.travelservice.dto.response.InvitationListDTO;
//import com.alphaka.travelservice.dto.response.InvitedListDTO;
//import com.alphaka.travelservice.service.InvitationService;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.*;
//
//class InvitationControllerTest {
//
//    private InvitationService invitationService;
//    private InvitationController invitationController;
//
//    @BeforeEach
//    void setUp() {
//        // Create a mock of the InvitationService
//        invitationService = mock(InvitationService.class);
//        // Initialize the InvitationController with the mocked service
//        invitationController = new InvitationController(invitationService);
//    }
//
//    @Test
//    void addInvitationByNick() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ParticipantRequest request = new ParticipantRequest("participantNickname");
//        Long expectedResponse = 1L;
//
//        when(invitationService.addInvitation(currentUser, request)).thenReturn(expectedResponse);
//
//        // Act
//        ApiResponse<Long> response = invitationController.addInvitationByNick(currentUser, request);
//
//        // Assert
//        assertEquals(expectedResponse, response.getData());
//        verify(invitationService, times(1)).addInvitation(currentUser, request);
//    }
//
//    @Test
//    void listInvitationById() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        List<InvitationListDTO> expectedResponse = new ArrayList<>();
//        expectedResponse.add(new InvitationListDTO(1L, "testInvite"));
//
//        when(invitationService.getInvitation(currentUser)).thenReturn(expectedResponse);
//
//        // Act
//        ApiResponse<List<InvitationListDTO>> response = invitationController.listInvitationById(currentUser);
//
//        // Assert
//        assertEquals(expectedResponse, response.getData());
//        verify(invitationService, times(1)).getInvitation(currentUser);
//    }
//
//    @Test
//    void accessInvitationByNick() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        InvitationDTO request = new InvitationDTO(1L, "ACCEPTED");
//        Long expectedResponse = 1L;
//
//        when(invitationService.changeInvitation(currentUser, request)).thenReturn(expectedResponse);
//
//        // Act
//        ApiResponse<Long> response = invitationController.accessInvitationByNick(currentUser, request);
//
//        // Assert
//        assertEquals(expectedResponse, response.getData());
//        verify(invitationService, times(1)).changeInvitation(currentUser, request);
//    }
//
//    @Test
//    void listInvitedByTravelId() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        Long travelId = 123L;
//        List<InvitedListDTO> expectedResponse = new ArrayList<>();
//        expectedResponse.add(new InvitedListDTO(1L, "invitedUser", "PENDING"));
//
//        when(invitationService.getInvited(currentUser, travelId)).thenReturn(expectedResponse);
//
//        // Act
//        ApiResponse<List<InvitedListDTO>> response = invitationController.listInvitedByTravelId(currentUser, travelId);
//
//        // Assert
//        assertEquals(expectedResponse, response.getData());
//        verify(invitationService, times(1)).getInvited(currentUser, travelId);
//    }
//}