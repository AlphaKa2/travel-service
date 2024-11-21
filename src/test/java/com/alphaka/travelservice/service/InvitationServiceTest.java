//package com.alphaka.travelservice.service;
//
//import com.alphaka.travelservice.client.UserClient;
//import com.alphaka.travelservice.common.dto.CurrentUser;
//import com.alphaka.travelservice.common.dto.UserDTO;
//import com.alphaka.travelservice.common.response.ApiResponse;
//import com.alphaka.travelservice.dto.request.ParticipantRequest;
//import com.alphaka.travelservice.entity.Invitations;
//import com.alphaka.travelservice.entity.TravelPlans;
//import com.alphaka.travelservice.exception.custom.DuplicateInvitationException;
//import com.alphaka.travelservice.exception.custom.InvitationAccessException;
//import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
//import com.alphaka.travelservice.repository.InvitationsRepository;
//import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class InvitationServiceTest {
//
//    private InvitationService invitationService;
//    private InvitationsRepository invitationsRepository;
//    private TravelPlansRepository travelPlansRepository;
//    private UserClient userClient;
//
//    @BeforeEach
//    void setUp() {
//        invitationsRepository = mock(InvitationsRepository.class);
//        travelPlansRepository = mock(TravelPlansRepository.class);
//        userClient = mock(UserClient.class);
//
//        invitationService = new InvitationService(
//                null, // queryFactory is not needed for this test
//                userClient,
//                travelPlansRepository,
//                invitationsRepository,
//                null // participantService is not needed for this test
//        );
//    }
//
//    @Test
//    void addInvitation_userNotFound_throwsException() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ParticipantRequest request = new ParticipantRequest(2L, "knownUser");
//
//        when(userClient.findUserByNickname("unknownUser"))
//                .thenThrow(new RuntimeException("User not found"));
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> invitationService.addInvitation(currentUser, request));
//    }
//
//    @Test
//    void addInvitation_travelPlanNotFound_throwsException() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ParticipantRequest request = new ParticipantRequest(2L, "knownUser");
//        ApiResponse<UserDTO> userResponse = new ApiResponse<>(new UserDTO(1L, "testUser", "test_profile.jpg"));
//
//        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
//        when(travelPlansRepository.findById(101L)).thenReturn(Optional.empty());
//
//        // Act & Assert
//        assertThrows(PlanNotFoundException.class, () -> invitationService.addInvitation(currentUser, request));
//    }
//
//    @Test
//    void addInvitation_noPermission_throwsException() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ApiResponse<UserDTO> userResponse = new ApiResponse<>(new UserDTO(1L, "testUser", "test_profile.jpg"));
//
//        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
//        when(travelPlansRepository.findById(101L)).thenReturn(Optional.of(travelPlan));
//
//        // Act & Assert
//        assertThrows(InvitationAccessException.class, () -> invitationService.addInvitation(currentUser, request));
//    }
//
//    @Test
//    void addInvitation_duplicateInvitation_throwsException() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ParticipantRequest request = new ParticipantRequest(2L, "knownUser");
//        TravelPlans travelPlan = new TravelPlans(101L, "Test Travel", 1L);
//        ApiResponse<UserDTO> userResponse = new ApiResponse<>(new UserDTO(2L, "invitee"));
//
//        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
//        when(travelPlansRepository.findById(101L)).thenReturn(Optional.of(travelPlan));
//        when(invitationsRepository.existsByUserIdAndTravelPlans(2L, travelPlan)).thenReturn(true);
//
//        // Act & Assert
//        assertThrows(DuplicateInvitationException.class, () -> invitationService.addInvitation(currentUser, request));
//    }
//
//    @Test
//    void addInvitation_successfullyCreatesInvitation() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ParticipantRequest request = new ParticipantRequest(2L, "knownUser");
//        TravelPlans travelPlan = new TravelPlans(101L, "Test Travel", 1L);
//        ApiResponse<UserDTO> userResponse = new ApiResponse<>(new UserDTO(2L, "invitee"));
//        Invitations newInvitation = Invitations.builder()
//                .travelPlans(travelPlan)
//                .userId(2L)
//                .invitationMessage("Test Travel")
//                .build();
//
//        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
//        when(travelPlansRepository.findById(101L)).thenReturn(Optional.of(travelPlan));
//        when(invitationsRepository.existsByUserIdAndTravelPlans(2L, travelPlan)).thenReturn(false);
//        when(invitationsRepository.save(any(Invitations.class))).thenReturn(newInvitation);
//
//        // Act
//        Long result = invitationService.addInvitation(currentUser, request);
//
//        // Assert
//        assertNotNull(result);
//        verify(invitationsRepository, times(1)).save(any(Invitations.class));
//    }
//
//    @Test
//    void addInvitation_missingInformation_throwsException() {
//        // Arrange
//        CurrentUser currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
//        ParticipantRequest request = new ParticipantRequest(2L, null); // Missing nickname
//
//        // Act & Assert
//        assertThrows(RuntimeException.class, () -> invitationService.addInvitation(currentUser, request));
//    }
//}
