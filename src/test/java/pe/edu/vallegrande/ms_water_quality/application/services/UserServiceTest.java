package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.ms_water_quality.domain.models.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Test
    void shouldCreateUserWithValidData() {
        // Given
        User user = new User();
        user.setUserId("1");
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());

        // When & Then
        assertNotNull(user);
        assertEquals("Test User", user.getName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("ACTIVE", user.getStatus());
    }

    @Test
    void shouldValidateEmailFormat() {
        // Given
        String validEmail = "test@example.com";
        String invalidEmail = "invalid-email";

        // When & Then
        assertTrue(isValidEmail(validEmail));
        assertFalse(isValidEmail(invalidEmail));
    }

    @Test
    void shouldSetUserStatus() {
        // Given
        User user = new User();
        
        // When
        user.setStatus("ACTIVE");
        
        // Then
        assertEquals("ACTIVE", user.getStatus());
    }

    private boolean isValidEmail(String email) {
        return email != null && 
               email.contains("@") && 
               email.contains(".") && 
               email.indexOf("@") > 0 && 
               email.lastIndexOf(".") > email.indexOf("@");
    }
}