package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
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

import static org.junit.jupiter.api.Assertions.*;
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

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "TECHNICIAN", "VIEWER", "MANAGER"})
    void shouldCreateUsersWithDifferentRoles(String role) {
        // Given
        User user = new User();
        user.setId("1");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setRole(role);
        user.setActive(true);

        UserCreateRequest request = new UserCreateRequest();
        request.setName("Test User");
        request.setEmail("test@example.com");
        request.setRole(role);

        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));

        // When
        Mono<UserResponse> result = userService.createUser(request);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getRole().equals(role))
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
        "admin@test.com, ADMIN, true",
        "tech@test.com, TECHNICIAN, true", 
        "viewer@test.com, VIEWER, false",
        "manager@test.com, MANAGER, true"
    })
    void shouldValidateUserEmailAndRoleCombinations(String email, String role, boolean active) {
        // Given
        User user = new User();
        user.setId("1");
        user.setName("Test User");
        user.setEmail(email);
        user.setRole(role);
        user.setActive(active);

        when(userRepository.findById(anyString())).thenReturn(Mono.just(user));

        // When
        Mono<UserResponse> result = userService.findById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getEmail().equals(email) &&
                    response.getRole().equals(role) &&
                    response.getActive().equals(active)
                )
                .verifyComplete();
    }

    @Test
    void shouldValidateEmailFormat() {
        // Given
        String[] validEmails = {"test@example.com", "user.name@domain.co.uk", "admin123@test.org"};
        String[] invalidEmails = {"invalid-email", "@domain.com", "user@", "user@.com"};

        // When & Then
        for (String email : validEmails) {
            assertTrue(isValidEmail(email), "Email should be valid: " + email);
        }

        for (String email : invalidEmails) {
            assertFalse(isValidEmail(email), "Email should be invalid: " + email);
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") && 
               email.indexOf("@") > 0 && 
               email.lastIndexOf(".") > email.indexOf("@");
    }
}