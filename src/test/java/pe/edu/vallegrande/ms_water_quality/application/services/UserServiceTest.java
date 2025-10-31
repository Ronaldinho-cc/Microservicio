package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.application.services.impl.UserServiceImpl;
import pe.edu.vallegrande.ms_water_quality.domain.models.User;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.UserCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.UserResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.repository.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("1");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setRole("ADMIN");
        testUser.setActive(true);

        createRequest = new UserCreateRequest();
        createRequest.setName("Test User");
        createRequest.setEmail("test@example.com");
        createRequest.setRole("ADMIN");
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));

        // When
        Mono<UserResponse> result = userService.createUser(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getName().equals("Test User") &&
                    response.getEmail().equals("test@example.com")
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(Flux.just(testUser));

        // When
        Flux<UserResponse> result = userService.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals("Test User"))
                .verifyComplete();
    }

    @Test
    void shouldFindUserById() {
        // Given
        when(userRepository.findById(anyString())).thenReturn(Mono.just(testUser));

        // When
        Mono<UserResponse> result = userService.findById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        when(userRepository.findById(anyString())).thenReturn(Mono.empty());

        // When
        Mono<UserResponse> result = userService.findById("999");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }
}