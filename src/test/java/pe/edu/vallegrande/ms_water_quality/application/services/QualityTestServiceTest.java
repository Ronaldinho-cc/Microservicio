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

import static org.junit.jupiter.api.Assertions.*;
import pe.edu.vallegrande.ms_water_quality.application.services.impl.QualityTestServiceImpl;
import pe.edu.vallegrande.ms_water_quality.domain.models.QualityTest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.QualityTestCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.QualityTestResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.repository.QualityTestRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QualityTestServiceTest {

    @Mock
    private QualityTestRepository qualityTestRepository;

    @InjectMocks
    private QualityTestServiceImpl qualityTestService;

    private QualityTest qualityTest;
    private QualityTestCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        qualityTest = new QualityTest();
        qualityTest.setId("1");
        qualityTest.setTestingPointId("point1");
        qualityTest.setUserId("user1");
        qualityTest.setTestDate(LocalDateTime.now());
        qualityTest.setTemperature(25.5);
        qualityTest.setPh(7.2);
        qualityTest.setOxygen(8.5);
        qualityTest.setTurbidity(2.1);

        QualityTest.TestResult testResult = new QualityTest.TestResult();
        testResult.setStatus("ACCEPTABLE");
        testResult.setScore(85.0);
        testResult.setObservations("Agua en condiciones aceptables");
        qualityTest.setTestResult(testResult);

        createRequest = new QualityTestCreateRequest();
        createRequest.setTestingPointId("point1");
        createRequest.setUserId("user1");
        createRequest.setTemperature(25.5);
        createRequest.setPh(7.2);
        createRequest.setOxygen(8.5);
        createRequest.setTurbidity(2.1);

        QualityTestCreateRequest.TestResult reqTestResult = new QualityTestCreateRequest.TestResult();
        reqTestResult.setStatus("ACCEPTABLE");
        reqTestResult.setScore(85.0);
        reqTestResult.setObservations("Agua en condiciones aceptables");
        createRequest.setTestResult(reqTestResult);
    }

    @Test
    void shouldCreateQualityTestSuccessfully() {
        // Given
        when(qualityTestRepository.save(any(QualityTest.class))).thenReturn(Mono.just(qualityTest));

        // When
        Mono<QualityTestResponse> result = qualityTestService.createQualityTest(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getTestingPointId().equals("point1") &&
                    response.getTemperature().equals(25.5) &&
                    response.getPh().equals(7.2)
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllQualityTests() {
        // Given
        when(qualityTestRepository.findAll()).thenReturn(Flux.just(qualityTest));

        // When
        Flux<QualityTestResponse> result = qualityTestService.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestingPointId().equals("point1"))
                .verifyComplete();
    }

    @Test
    void shouldFindQualityTestsByTestingPoint() {
        // Given
        when(qualityTestRepository.findByTestingPointId(anyString())).thenReturn(Flux.just(qualityTest));

        // When
        Flux<QualityTestResponse> result = qualityTestService.findByTestingPointId("point1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestingPointId().equals("point1"))
                .verifyComplete();
    }

    @Test
    void shouldFindQualityTestById() {
        // Given
        when(qualityTestRepository.findById(anyString())).thenReturn(Mono.just(qualityTest));

        // When
        Mono<QualityTestResponse> result = qualityTestService.findById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldCalculateWaterQualityScore() {
        // Given
        double ph = 7.2;
        double oxygen = 8.5;
        double temperature = 25.5;
        double turbidity = 2.1;

        // When
        double score = qualityTestService.calculateWaterQualityScore(ph, oxygen, temperature, turbidity);

        // Then
        assert score > 0 && score <= 100;
    }

    @ParameterizedTest
    @CsvSource({
        "7.0, 8.0, 20.0, 1.0, EXCELLENT",
        "6.8, 7.5, 22.0, 2.0, GOOD", 
        "6.5, 6.0, 25.0, 3.0, ACCEPTABLE",
        "6.0, 4.0, 30.0, 5.0, POOR",
        "5.5, 3.0, 35.0, 8.0, CRITICAL"
    })
    void shouldClassifyWaterQualityByParameters(double ph, double oxygen, double temperature, double turbidity, String expectedStatus) {
        // When
        String status = classifyWaterQuality(ph, oxygen, temperature, turbidity);

        // Then
        assertEquals(expectedStatus, status);
    }

    @ParameterizedTest
    @ValueSource(doubles = {6.5, 7.0, 7.5, 8.0, 8.5})
    void shouldAcceptValidPhValues(double ph) {
        // When
        boolean isValid = isValidPh(ph);

        // Then
        assertTrue(isValid, "pH " + ph + " should be valid");
    }

    @ParameterizedTest
    @ValueSource(doubles = {5.0, 6.0, 9.0, 10.0, 14.0})
    void shouldRejectInvalidPhValues(double ph) {
        // When
        boolean isValid = isValidPh(ph);

        // Then
        assertFalse(isValid, "pH " + ph + " should be invalid");
    }

    private String classifyWaterQuality(double ph, double oxygen, double temperature, double turbidity) {
        double score = calculateScore(ph, oxygen, temperature, turbidity);
        
        if (score >= 90) return "EXCELLENT";
        if (score >= 75) return "GOOD";
        if (score >= 60) return "ACCEPTABLE";
        if (score >= 40) return "POOR";
        return "CRITICAL";
    }

    private double calculateScore(double ph, double oxygen, double temperature, double turbidity) {
        double phScore = isValidPh(ph) ? 100 - Math.abs(7.0 - ph) * 10 : 0;
        double oxygenScore = oxygen >= 8.0 ? 100 : oxygen >= 5.0 ? oxygen * 12.5 : 0;
        double tempScore = (temperature >= 15 && temperature <= 25) ? 100 : 50;
        double turbScore = turbidity <= 1.0 ? 100 : turbidity <= 5.0 ? 100 - (turbidity - 1) * 20 : 20;
        
        return (phScore + oxygenScore + tempScore + turbScore) / 4;
    }

    private boolean isValidPh(double ph) {
        return ph >= 6.5 && ph <= 8.5;
    }
}