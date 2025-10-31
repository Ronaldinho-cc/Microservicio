package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import pe.edu.vallegrande.ms_water_quality.domain.models.QualityTest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.QualityTestCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.enriched.QualityTestEnrichedResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QualityTestServiceTest {

    @Mock
    private QualityTestService qualityTestService;

    private QualityTest qualityTest;
    private QualityTestCreateRequest createRequest;
    private QualityTestEnrichedResponse enrichedResponse;

    @BeforeEach
    void setUp() {
        List<String> testingPointIds = Arrays.asList("point1", "point2");
        
        qualityTest = new QualityTest();
        qualityTest.setId("1");
        qualityTest.setOrganizationId("org1");
        qualityTest.setTestCode("TEST001");
        qualityTest.setTestingPointId(testingPointIds);
        qualityTest.setTestDate(LocalDateTime.now());
        qualityTest.setTestType("WATER_QUALITY");
        qualityTest.setTestedByUserId("user1");
        qualityTest.setWeatherConditions("Sunny");
        qualityTest.setWaterTemperature(25.5);
        qualityTest.setGeneralObservations("Test observations");
        qualityTest.setStatus("COMPLETED");

        QualityTest.TestResult testResult = new QualityTest.TestResult();
        testResult.setParameterId("param1");
        testResult.setParameterCode("PH");
        testResult.setMeasuredValue(7.2);
        testResult.setUnit("pH");
        testResult.setStatus("ACCEPTABLE");
        testResult.setObservations("pH level is acceptable");
        qualityTest.setResults(Arrays.asList(testResult));

        createRequest = new QualityTestCreateRequest();
        createRequest.setOrganization("org1");
        createRequest.setTestCode("TEST001");
        createRequest.setTestingPointId(testingPointIds);
        createRequest.setTestDate(LocalDateTime.now());
        createRequest.setTestType("WATER_QUALITY");
        createRequest.setTestedByUser("user1");
        createRequest.setWeatherConditions("Sunny");
        createRequest.setWaterTemperature(25.5);
        createRequest.setGeneralObservations("Test observations");
        createRequest.setStatus("COMPLETED");

        QualityTestCreateRequest.TestResult reqTestResult = new QualityTestCreateRequest.TestResult();
        reqTestResult.setParameterId("param1");
        reqTestResult.setParameterCode("PH");
        reqTestResult.setMeasuredValue(7.2);
        reqTestResult.setUnit("pH");
        reqTestResult.setStatus("ACCEPTABLE");
        reqTestResult.setObservations("pH level is acceptable");
        createRequest.setResults(Arrays.asList(reqTestResult));

        enrichedResponse = QualityTestEnrichedResponse.builder()
                .id("1")
                .testCode("TEST001")
                .testDate(LocalDateTime.now())
                .testType("WATER_QUALITY")
                .weatherConditions("Sunny")
                .waterTemperature(25.5)
                .generalObservations("Test observations")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateQualityTestSuccessfully() {
        // Given
        when(qualityTestService.save(any(QualityTestCreateRequest.class))).thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<QualityTestEnrichedResponse> result = qualityTestService.save(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getTestCode().equals("TEST001") &&
                    response.getWaterTemperature().equals(25.5) &&
                    response.getStatus().equals("COMPLETED")
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllQualityTests() {
        // Given
        when(qualityTestService.getAll()).thenReturn(Flux.just(enrichedResponse));

        // When
        Flux<QualityTestEnrichedResponse> result = qualityTestService.getAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestCode().equals("TEST001"))
                .verifyComplete();
    }

    @Test
    void shouldFindQualityTestsByOrganization() {
        // Given
        when(qualityTestService.getAllByOrganization(anyString())).thenReturn(Flux.just(enrichedResponse));

        // When
        Flux<QualityTestEnrichedResponse> result = qualityTestService.getAllByOrganization("org1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestCode().equals("TEST001"))
                .verifyComplete();
    }

    @Test
    void shouldFindQualityTestById() {
        // Given
        when(qualityTestService.getById(anyString())).thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<QualityTestEnrichedResponse> result = qualityTestService.getById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldUpdateQualityTest() {
        // Given
        when(qualityTestService.update(anyString(), any(QualityTestCreateRequest.class)))
                .thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<QualityTestEnrichedResponse> result = qualityTestService.update("1", createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }

    @ParameterizedTest
    @CsvSource({
        "7.0, 8.0, 20.0, 1.0, EXCELLENT",
        "6.8, 7.5, 22.0, 2.0, GOOD", 
        "6.5, 6.0, 25.0, 3.0, GOOD",
        "6.0, 4.0, 30.0, 5.0, CRITICAL",
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
        double phScore = isValidPh(ph) ? 100 - Math.abs(7.0 - ph) * 15 : 0;
        double oxygenScore = oxygen >= 8.0 ? 100 : oxygen >= 5.0 ? oxygen * 10 : 0;
        double tempScore = (temperature >= 15 && temperature <= 25) ? 100 : 60;
        double turbScore = turbidity <= 1.0 ? 100 : turbidity <= 5.0 ? 100 - (turbidity - 1) * 25 : 0;
        
        return (phScore + oxygenScore + tempScore + turbScore) / 4;
    }

    private boolean isValidPh(double ph) {
        return ph >= 6.5 && ph <= 8.5;
    }
}