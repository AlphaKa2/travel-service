package com.alphaka.travelservice.service;

import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.TravelPlansTest;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.entity.Invitations;
import com.alphaka.travelservice.entity.TravelPlans;
import com.alphaka.travelservice.entity.TravelStatus;
import com.alphaka.travelservice.entity.TravelType;
import com.alphaka.travelservice.exception.custom.DuplicateInvitationException;
import com.alphaka.travelservice.exception.custom.InvitationAccessException;
import com.alphaka.travelservice.exception.custom.PlanNotFoundException;
import com.alphaka.travelservice.exception.custom.UserNotFoundException;
import com.alphaka.travelservice.repository.invitation.InvitationsRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import com.alphaka.travelservice.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InvitationServiceTest {

    @Mock
    private InvitationService invitationService;
    @Mock
    private InvitationsRepository invitationsRepository;
    @Mock
    private TravelPlansRepository travelPlansRepository;
    @Mock
    private UserClient userClient;

    private CurrentUser currentUser;
    private ParticipantRequest request;
    private TravelPlansTest travelPlan;
    private ApiResponse<UserDTO> userResponse;
    private Invitations newInvitation;

    @BeforeEach
    void setUp() {
        // Mock Initialization
        invitationsRepository = mock(InvitationsRepository.class);
        travelPlansRepository = mock(TravelPlansRepository.class);
        userClient = mock(UserClient.class);

        // Initialize InvitationService with Mock Dependencies
        invitationService = new InvitationService(
                null, // queryFactory is not needed for this test
                userClient,
                travelPlansRepository,
                invitationsRepository,
                null // participantService is not needed for this test
        );

        // Create Objects for Tests
        currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
        request = new ParticipantRequest(101L, "invitee");
        travelPlan = new TravelPlansTest(101L, "Test Travel", 2L, TravelType.USER_GENERATED, TravelStatus.PLANNED);
        userResponse = new ApiResponse<>(new UserDTO(2L, "invitee", "invitee_profile.jpg"));
        newInvitation = Invitations.builder()
                .travelPlans(travelPlan)
                .userId(1L)
                .invitationMessage("Test Travel")
                .build();

        // Use TestUtil to Set Fields
        TestUtil.setField(invitationService, "invitationsRepository", invitationsRepository);
        TestUtil.setField(invitationService, "travelPlansRepository", travelPlansRepository);
        TestUtil.setField(invitationService, "userClient", userClient);
    }

    @Test
    void addInvitation_userNotFound_throwsException() {
        // Arrange
        when(userClient.findUserByNickname("unknownUser"))
                .thenThrow(new UserNotFoundException());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> invitationService.addInvitation(currentUser, request));
    }

    @Test
    void addInvitation_travelPlanNotFound_throwsException() {
        // Arrange
        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
        when(travelPlansRepository.findById(request.getTravelId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PlanNotFoundException.class, () -> invitationService.addInvitation(currentUser, request));
    }

    @Test
    void addInvitation_noPermission_throwsException() {
        // Arrange
        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
        when(travelPlansRepository.findById(101L)).thenReturn(Optional.of(travelPlan));

        // Act & Assert
        assertThrows(InvitationAccessException.class, () -> invitationService.addInvitation(currentUser, request));

        // Verify
        verify(userClient).findUserByNickname("invitee");
        verify(travelPlansRepository).findById(101L);
    }

    @Test
    void addInvitation_duplicateInvitation_throwsException() {

        travelPlan = new TravelPlansTest(101L, "Test Travel", 1L);
        newInvitation = Invitations.builder()
                .travelPlans(travelPlan)
                .userId(2L)
                .invitationMessage("Test Travel")
                .build();
        // Arrange
        when(userClient.findUserByNickname("invitee")).thenReturn(userResponse);
        when(travelPlansRepository.findById(101L)).thenReturn(Optional.of(travelPlan));
        when(invitationsRepository.existsByUserIdAndTravelPlans(2L, travelPlan)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateInvitationException.class, () -> invitationService.addInvitation(currentUser, request));
    }

//    @Test
//    void addInvitation_successfullyCreatesInvitation() {
//
//
//        // Arrange
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

    @Test
    void addInvitation_missingInformation_throwsException() {
        // Arrange
        ParticipantRequest invalidRequest = new ParticipantRequest(2L, null); // Missing nickname

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> invitationService.addInvitation(currentUser, invalidRequest));
    }
}
