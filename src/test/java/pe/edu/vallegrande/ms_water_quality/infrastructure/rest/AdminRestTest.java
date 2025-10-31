package pe.edu.vallegrande.ms_water_quality.infrastructure.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.application.services.UserService;
import pe.edu.vallegrande.ms_water_quality.application.services.TestingPointService;
import pe.edu.vallegrande.ms_water_quality.application.services.QualityTestService;
import pe.edu.vallegrande.ms_water_quality.application.services.DailyRecordService;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.ResponseDto;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.UserResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.rest.admin.AdminRest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRestTest {

    @Mock
    private UserService userService;

    @Mock
    private TestingPointService testingPointService;

    @Mock
    private QualityTestService qualityTestService;

    @Mock
    private DailyRecordService dailyRecordService;

    @InjectMocks
    private AdminRest adminRest;

    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        userResponse = new UserResponse();
        userResponse.setId("1");
        userResponse.setName("Test User");
        userResponse.setEmail("test@example.com");
        userResponse.setRole("ADMIN");
        userResponse.setActive(true);
    }

    @Test
    void shouldGetAllUsers() {
        // Given
        when(userService.findAll()).thenReturn(Flux.just(userResponse));

        // When
        Mono<ResponseDto<Flux<UserResponse>>> result = adminRest.getAllUsers();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.isSuccess() && 
                    response.getMessage().equals("Usuarios obtenidos exitosamente")
                )
                .verifyComplete();
    }

    @Test
    void shouldGetUserById() {
        // Given
        when(userService.findById("1")).thenReturn(Mono.just(userResponse));

        // When
        Mono<ResponseDto<UserResponse>> result = adminRest.getUserById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.isSuccess() && 
                    response.getData().getId().equals("1")
                )
                .verifyComplete();
    }

    @Test
    void shouldHandleUserNotFound() {
        // Given
        when(userService.findById("999")).thenReturn(Mono.empty());

        // When
        Mono<ResponseDto<UserResponse>> result = adminRest.getUserById("999");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    !response.isSuccess() && 
                    response.getMessage().equals("Usuario no encontrado")
                )
                .verifyComplete();
    }
}