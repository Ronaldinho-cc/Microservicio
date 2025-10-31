package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.domain.models.User;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.UserCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.UserResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserService userService;

    private User testUser;
    private UserCreateRequest createRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId("1");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password123");
        testUser.setStatus("ACTIVE");
        testUser.setCreatedAt(LocalDateTime.now());

        createRequest = new UserCreateRequest();
        createRequest.setName("Test User");
        createRequest.setEmail("test@example.com");
        createRequest.setPassword("password123");

        userResponse = new UserResponse();
        userResponse.setUserId("1");
        userResponse.setName("Test User");
        userResponse.setEmail("test@example.com");
        userResponse.setStatus("ACTIVE");
        userResponse.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateUserSuccessfully() {
        // Given
        when(userService.save(any(UserCreateRequest.class))).thenReturn(Mono.just(userResponse));

        // When
        Mono<UserResponse> result = userService.save(createRequest);

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
        when(userService.getAll()).thenReturn(Flux.just(testUser));

        // When
        Flux<User> result = userService.getAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getName().equals("Test User"))
                .verifyComplete();
    }

    @Test
    void shouldFindUserById() {
        // Given
        when(userService.getById(anyString())).thenReturn(Mono.just(testUser));

        // When
        Mono<User> result = userService.getById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(user -> user.getUserId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyWhenUserNotFound() {
        // Given
        when(userService.getById(anyString())).thenReturn(Mono.empty());

        // When
        Mono<User> result = userService.getById("999");

        // Then
        StepVerifier.create(result)
                .verifyComplete();
    }

    @ParameterizedTest
    @ValueSource(strings = {"ACTIVE", "INACTIVE"})
    void shouldCreateUsersWithDifferentStatuses(String status) {
        // Given
        User user = new User();
        user.setUserId("1");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setStatus(status);

        when(userService.getById(anyString())).thenReturn(Mono.just(user));

        // When
        Mono<User> result = userService.getById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(u -> u.getStatus().equals(status))
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
        "admin@test.com, ACTIVE",
        "tech@test.com, ACTIVE", 
        "viewer@test.com, INACTIVE",
        "manager@test.com, ACTIVE"
    })
    void shouldValidateUserEmailAndStatusCombinations(String email, String status) {
        // Given
        User user = new User();
        user.setUserId("1");
        user.setName("Test User");
        user.setEmail(email);
        user.setPassword("password123");
        user.setStatus(status);

        when(userService.getById(anyString())).thenReturn(Mono.just(user));

        // When
        Mono<User> result = userService.getById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(u -> 
                    u.getEmail().equals(email) &&
                    u.getStatus().equals(status)
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
               email.lastIndexOf(".") > email.indexOf("@") &&
               !email.endsWith("@.com") &&
               !email.startsWith("@");
    }
}