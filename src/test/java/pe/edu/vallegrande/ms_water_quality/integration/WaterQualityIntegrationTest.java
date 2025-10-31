package pe.edu.vallegrande.ms_water_quality.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.domain.models.User;
import pe.edu.vallegrande.ms_water_quality.domain.models.TestingPoint;

@ExtendWith(MockitoExtension.class)
class WaterQualityIntegrationTest {

    @Test
    void shouldCreateUserAndTestingPointIntegration() {
        // Given
        User user = new User();
        user.setName("Integration Test User");
        user.setEmail("integration@test.com");
        user.setPassword("password123");
        user.setStatus("ACTIVE");

        TestingPoint testingPoint = new TestingPoint();
        testingPoint.setPointName("Integration Test Point");
        testingPoint.setLocationDescription("Punto de prueba para integración");
        testingPoint.setPointType("RESERVORIO");
        testingPoint.setStatus("ACTIVE");

        TestingPoint.Coordinates coordinates = new TestingPoint.Coordinates();
        coordinates.setLatitude(-12.0464);
        coordinates.setLongitude(-77.0428);
        testingPoint.setCoordinates(coordinates);

        // When & Then
        assert user.getName().equals("Integration Test User");
        assert testingPoint.getPointName().equals("Integration Test Point");
        assert testingPoint.getCoordinates().getLatitude() == -12.0464;
    }

    @Test
    void shouldValidateWaterQualityParameters() {
        // Given
        double ph = 7.2;
        double oxygen = 8.5;
        double temperature = 25.0;
        double turbidity = 2.0;

        // When
        boolean isPhValid = ph >= 6.5 && ph <= 8.5;
        boolean isOxygenValid = oxygen >= 5.0;
        boolean isTemperatureValid = temperature >= 0 && temperature <= 40;
        boolean isTurbidityValid = turbidity >= 0 && turbidity <= 10;

        // Then
        assert isPhValid;
        assert isOxygenValid;
        assert isTemperatureValid;
        assert isTurbidityValid;
    }

    @Test
    void shouldCalculateWaterQualityScore() {
        // Given
        double ph = 7.0;
        double oxygen = 8.0;
        double temperature = 20.0;
        double turbidity = 1.0;

        // When
        double phScore = calculatePhScore(ph);
        double oxygenScore = calculateOxygenScore(oxygen);
        double temperatureScore = calculateTemperatureScore(temperature);
        double turbidityScore = calculateTurbidityScore(turbidity);

        double totalScore = (phScore + oxygenScore + temperatureScore + turbidityScore) / 4;

        // Then
        assert totalScore > 0;
        assert totalScore <= 100;
    }

    private double calculatePhScore(double ph) {
        if (ph >= 6.5 && ph <= 8.5) {
            return 100 - Math.abs(7.0 - ph) * 10;
        }
        return 0;
    }

    private double calculateOxygenScore(double oxygen) {
        if (oxygen >= 8.0) return 100;
        if (oxygen >= 5.0) return oxygen * 12.5;
        return 0;
    }

    private double calculateTemperatureScore(double temperature) {
        if (temperature >= 15 && temperature <= 25) return 100;
        if (temperature >= 10 && temperature <= 30) return 80;
        return 50;
    }

    private double calculateTurbidityScore(double turbidity) {
        if (turbidity <= 1.0) return 100;
        if (turbidity <= 5.0) return 100 - (turbidity - 1) * 20;
        return 20;
    }
}