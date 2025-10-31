package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
}