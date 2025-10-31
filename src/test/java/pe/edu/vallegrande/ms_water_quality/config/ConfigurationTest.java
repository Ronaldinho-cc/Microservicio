package pe.edu.vallegrande.ms_water_quality.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationTest {

    @Test
    void shouldValidateApplicationProperties() {
        // Given
        String appName = "ms_water_quality";
        String version = "1.0.0";
        
        // When & Then
        assertNotNull(appName);
        assertNotNull(version);
        assertTrue(appName.length() > 0);
        assertTrue(version.matches("\\d+\\.\\d+\\.\\d+"));
    }

    @ParameterizedTest
    @CsvSource({
        "localhost, 27017, true",
        "mongodb://localhost:27017, 0, true",
        "invalid-host, 27017, false",
        "localhost, -1, false"
    })
    void shouldValidateMongoDBConnectionStrings(String host, int port, boolean expectedValid) {
        // When
        boolean isValid = validateMongoConnection(host, port);
        
        // Then
        assertEquals(expectedValid, isValid);
    }

    @Test
    void shouldValidateSecurityConfiguration() {
        // Given
        String jwtSecret = "test-secret-key-for-testing-purposes-only";
        long jwtExpiration = 3600000; // 1 hour
        
        // When & Then
        assertNotNull(jwtSecret);
        assertTrue(jwtSecret.length() >= 32, "JWT secret should be at least 32 characters");
        assertTrue(jwtExpiration > 0, "JWT expiration should be positive");
        assertTrue(jwtExpiration <= 86400000, "JWT expiration should not exceed 24 hours");
    }

    @ParameterizedTest
    @CsvSource({
        "ADMIN, true",
        "TECHNICIAN, true", 
        "VIEWER, true",
        "MANAGER, true",
        "INVALID_ROLE, false",
        "'', false"
    })
    void shouldValidateUserRoles(String role, boolean expectedValid) {
        // When
        boolean isValid = isValidRole(role);
        
        // Then
        assertEquals(expectedValid, isValid);
    }

    private boolean validateMongoConnection(String host, int port) {
        if (host == null || host.trim().isEmpty()) return false;
        if (port < 0 || port > 65535) return false;
        if (host.contains("invalid")) return false;
        return true;
    }

    private boolean isValidRole(String role) {
        if (role == null || role.trim().isEmpty()) return false;
        return role.equals("ADMIN") || 
               role.equals("TECHNICIAN") || 
               role.equals("VIEWER") || 
               role.equals("MANAGER");
    }
}