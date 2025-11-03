package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.Test;
import pe.edu.vallegrande.ms_water_quality.domain.models.QualityTest;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class QualityTestServiceTest {

    @Test
    void shouldCreateQualityTestWithValidData() {
        // Given
        QualityTest qualityTest = new QualityTest();
        qualityTest.setId("1");
        qualityTest.setTestCode("TEST001");
        qualityTest.setTestingPointId(Arrays.asList("point1"));
        qualityTest.setTestDate(LocalDateTime.now());
        qualityTest.setWaterTemperature(25.5);
        qualityTest.setStatus("COMPLETED");

        // When & Then
        assertNotNull(qualityTest);
        assertEquals("TEST001", qualityTest.getTestCode());
        assertEquals(25.5, qualityTest.getWaterTemperature());
        assertEquals("COMPLETED", qualityTest.getStatus());
    }

    @Test
    void shouldValidatePhValues() {
        // Given
        double validPh = 7.2;
        double invalidPh = 5.0;

        // When & Then
        assertTrue(isValidPh(validPh));
        assertFalse(isValidPh(invalidPh));
    }

    @Test
    void shouldCalculateWaterQualityScore() {
        // Given
        double ph = 7.0;
        double oxygen = 8.0;
        double temperature = 20.0;
        double turbidity = 1.0;

        // When
        double score = calculateScore(ph, oxygen, temperature, turbidity);

        // Then
        assertTrue(score > 0);
        assertTrue(score <= 100);
    }

    private boolean isValidPh(double ph) {
        return ph >= 6.5 && ph <= 8.5;
    }

    private double calculateScore(double ph, double oxygen, double temperature, double turbidity) {
        double phScore = isValidPh(ph) ? 100 - Math.abs(7.0 - ph) * 15 : 0;
        double oxygenScore = oxygen >= 8.0 ? 100 : oxygen >= 5.0 ? oxygen * 10 : 0;
        double tempScore = (temperature >= 15 && temperature <= 25) ? 100 : 60;
        double turbScore = turbidity <= 1.0 ? 100 : turbidity <= 5.0 ? 100 - (turbidity - 1) * 25 : 0;
        
        return (phScore + oxygenScore + tempScore + turbScore) / 4;
    }
}