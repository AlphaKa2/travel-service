package com.alphaka.travelservice.service;

import com.alphaka.travelservice.TestUtil;
import com.alphaka.travelservice.client.UserClient;
import com.alphaka.travelservice.common.dto.CurrentUser;
import com.alphaka.travelservice.common.dto.UserDTO;
import com.alphaka.travelservice.common.response.ApiResponse;
import com.alphaka.travelservice.dto.TravelPlansTest;
import com.alphaka.travelservice.dto.request.ParticipantChangeRequest;
import com.alphaka.travelservice.dto.request.ParticipantRequest;
import com.alphaka.travelservice.dto.response.ParticipantListDTO;
import com.alphaka.travelservice.entity.*;
import com.alphaka.travelservice.exception.custom.*;
import com.alphaka.travelservice.repository.invitation.InvitationsRepository;
import com.alphaka.travelservice.repository.invitation.ParticipantsRepository;
import com.alphaka.travelservice.repository.travel.TravelPlansRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ParticipantServiceTest {

    @InjectMocks
    private ParticipantService participantService;

    @Mock
    private TravelPlansRepository travelPlansRepository;

    @Mock
    private ParticipantsRepository participantsRepository;

    @Mock
    private UserClient userClient;

    @Mock
    private JPAQueryFactory queryFactory;

    private AutoCloseable closeable;

    private CurrentUser currentUser;
    private ParticipantRequest request;
    private TravelPlansTest travelPlan;
    private ApiResponse<UserDTO> userResponse;
    private Invitations newInvitation;

    private Participants participant;


    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);

        // Create Objects for Tests
        currentUser = new CurrentUser(1L, "testUser", "test_profile", "test_nick");
        request = new ParticipantRequest(101L, "invitee");
        travelPlan = new TravelPlansTest(101L, "Test Travel", 1L, TravelType.USER_GENERATED, TravelStatus.PLANNED);
        userResponse = new ApiResponse<>(new UserDTO(2L, "invitee", "invitee_profile.jpg"));
        newInvitation = Invitations.builder()
                .travelPlans(travelPlan)
                .userId(1L)
                .invitationMessage("Test Travel")
                .build();
        participant = Participants.builder()
                .userId(1L)
                .travelPlans(travelPlan)
                .permission(Permission.EDIT)
                .build();
        TestUtil.setField(participant, "participantId", 201L); // participantId 설정
    }


    @Test
    void addSelfParticipant_success() {
        // Arrange

        when(travelPlansRepository.findById(travelPlan.getTravelId())).thenReturn(Optional.of(travelPlan));

        // Act
        participantService.addSelfParticipant(currentUser.getUserId(), travelPlan.getTravelId());

        // Assert
        verify(participantsRepository, times(1)).save(any(Participants.class));
    }

    @Test
    void addSelfParticipant_planNotFound_throwsException() {
        // Arrange
        Long userId = 1L;
        Long travelId = 2L;

        when(travelPlansRepository.findById(travelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PlanNotFoundException.class, () -> participantService.addSelfParticipant(currentUser.getUserId(), travelId));
    }

    @Test
    void addParticipant_success() {
        // Arrange
        Long userId = 3L;

        when(travelPlansRepository.findById(travelPlan.getTravelId())).thenReturn(Optional.of(travelPlan));
        when(participantsRepository.save(any(Participants.class))).thenReturn(participant);

        // Act
        participantService.addParticipant(userId, travelPlan.getTravelId());

        // Assert
        verify(participantsRepository, times(1)).save(any(Participants.class));
    }

    @Test
    void addParticipant_planNotFound_throwsException() {
        // Arrange
        Long userId = 3L;
        Long travelId = 2L;

        when(travelPlansRepository.findById(travelId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(PlanNotFoundException.class, () -> participantService.addParticipant(userId, travelId));
    }

//    @Test
//    void getParticipant_success() {
//        // Mock 설정: 현재 사용자가 참여자인지 확인
//        JPAQuery<Participants> participantCheckQuery = mock(JPAQuery.class);
//        when(queryFactory.selectFrom(QParticipants.participants)).thenReturn(participantCheckQuery);
//        when(participantCheckQuery.where(any(Predicate.class))).thenReturn(participantCheckQuery);
//        when(participantCheckQuery.fetchFirst()).thenReturn(participant); // 참여자로 설정
//
//        // Mock 설정: 여행지에 연관된 참여자 목록 조회
//        JPAQuery<Participants> travelParticipantsQuery = mock(JPAQuery.class);
//        when(queryFactory.selectFrom(QParticipants.participants)).thenReturn(travelParticipantsQuery);
//        when(travelParticipantsQuery.where(any(Predicate.class))).thenReturn(travelParticipantsQuery);
//        when(travelParticipantsQuery.fetch()).thenReturn(List.of(participant));
//
//        // Mock 설정: UserClient 호출
//        UserDTO userDTO = new UserDTO(currentUser.getUserId(), currentUser.getNickname(), currentUser.getProfileImage());
//        when(userClient.findUserById(currentUser.getUserId())).thenReturn(new ApiResponse<>(userDTO));
//
//        // 테스트 실행
//        List<ParticipantListDTO> participants = participantService.getParticipant(currentUser, travelPlan.getTravelId());
//
//        // 결과 검증
//        assertNotNull(participants);
//        assertEquals(1, participants.size());
//        assertEquals(currentUser.getNickname(), participants.get(0).getNickname());
//    }




    @Test
    void getParticipant_accessDenied() {
        // Arrange
        Long travelId = 2L;
        currentUser = new CurrentUser(2L, "NoAccessUser", "test_profile", "test_nick");

        QParticipants qParticipants = QParticipants.participants; // QParticipants 인스턴스 생성

        // Mock: queryFactory.selectFrom(qParticipants)
        JPAQuery<Participants> mockQuery = mock(JPAQuery.class);
        when(queryFactory.selectFrom(qParticipants)).thenReturn(mockQuery);

        // Mock: mockQuery.where(...) 체이닝 호출
        when(mockQuery.where((Predicate) any())).thenReturn(mockQuery); // where() 호출 결과
        when(mockQuery.fetchFirst()).thenReturn(null); // fetchFirst()가 null 반환

        // Act & Assert
        assertThrows(ParticipantAccessException.class, () -> {
            participantService.getParticipant(currentUser, travelId);
        });
    }



    @Test
    void changeParticipant_success() {
        // Arrange
        // spy 개체는 실제 개체를 래핑하고 해당 메서드 호출을 모니터링할 수 있게 해줌. 이 경우 Participants 객체를 래핑함.
        Participants newParticipant = spy(Participants.builder()
                .userId(2L)
                .travelPlans(travelPlan)
                .permission(Permission.VIEW) // 초기 permission
                .build());
        TestUtil.setField(newParticipant, "participantId", 202L); // participantId 설정

        ParticipantChangeRequest changeRequest = new ParticipantChangeRequest(newParticipant.getParticipantId(), Permission.EDIT);

        when(participantsRepository.findById(newParticipant.getParticipantId()))
                .thenReturn(Optional.of(newParticipant));
        when(participantsRepository.save(any(Participants.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // 저장된 객체 반환

        // Act
        Long changedId = participantService.changeParticipant(currentUser, changeRequest);

        // Assert
        assertEquals(newParticipant.getParticipantId(), changedId);
        assertEquals(Permission.EDIT, newParticipant.getPermission()); // Permission이 EDIT로 업데이트되었는지 확인
        verify(participantsRepository, times(1)).findById(newParticipant.getParticipantId());
        verify(newParticipant).changeParticipantPermission(Permission.EDIT);
    }


    @Test
    void changeParticipant_accessDenied_throwsException() {
        // Arrange
        currentUser = new CurrentUser(2L, "NoAccessUser", "test_profile", "test_nick");
        Long currentUserId = 2L;
        Participants newParticipant = spy(Participants.builder()
                .userId(2L)
                .travelPlans(travelPlan)
                .permission(Permission.VIEW) // 초기 permission
                .build());
        TestUtil.setField(newParticipant, "participantId", 202L); // participantId 설정


        ParticipantChangeRequest request = new ParticipantChangeRequest(newParticipant.getParticipantId(), Permission.EDIT);

        when(participantsRepository.findById(newParticipant.getParticipantId())).thenReturn(Optional.of(newParticipant));

        // Act & Assert
        assertThrows(ParticipantAccessException.class, () -> participantService.changeParticipant(currentUser, request));
    }

    @Test
    void deleteParticipant_success() {
        // Arrange
        Participants newParticipant = spy(Participants.builder()
                .userId(2L)
                .travelPlans(travelPlan)
                .permission(Permission.VIEW) // 초기 permission
                .build());
        TestUtil.setField(newParticipant, "participantId", 202L); // participantId 설정

        when(participantsRepository.findById(newParticipant.getParticipantId())).thenReturn(Optional.of(newParticipant));

        // Act
        Long deletedId = participantService.deleteParticipant(currentUser, newParticipant.getParticipantId());

        // Assert
        assertEquals(newParticipant.getParticipantId(), deletedId);
        verify(participantsRepository, times(1)).delete(newParticipant);
    }

    @Test
    void deleteParticipant_accessDenied_throwsException() {
        // Arrange
        currentUser = new CurrentUser(2L, "NoAccessUser", "test_profile", "test_nick");
        Participants newParticipant = spy(Participants.builder()
                .userId(2L)
                .travelPlans(travelPlan)
                .permission(Permission.VIEW) // 초기 permission
                .build());
        TestUtil.setField(newParticipant, "participantId", 202L); // participantId 설정

        when(participantsRepository.findById(newParticipant.getParticipantId())).thenReturn(Optional.of(newParticipant));

        // Act & Assert
        assertThrows(ParticipantAccessException.class, () -> participantService.deleteParticipant(currentUser, newParticipant.getParticipantId()));
    }

    private JPAQuery<Participants> mockQueryFactoryForParticipant(Long travelId, Long userId) {
        // Mock JPAQuery 객체 생성
        JPAQuery<Participants> mockQuery = mock(JPAQuery.class);

        // 필요한 동작 설정
        when(mockQuery.fetch()).thenReturn(
                List.of(
                        new Participants(travelPlan, userId, Permission.VIEW)
                )
        );

        return mockQuery;
    }
}
