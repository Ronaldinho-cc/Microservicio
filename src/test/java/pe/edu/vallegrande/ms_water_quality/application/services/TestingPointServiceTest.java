package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.application.services.impl.TestingPointServiceImpl;
import pe.edu.vallegrande.ms_water_quality.domain.models.TestingPoint;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.TestingPointCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.TestingPointResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.repository.TestingPointRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestingPointServiceTest {

    @Mock
    private TestingPointRepository testingPointRepository;

    @InjectMocks
    private TestingPointServiceImpl testingPointService;

    private TestingPoint testingPoint;
    private TestingPointCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        testingPoint = new TestingPoint();
        testingPoint.setId("1");
        testingPoint.setName("Punto de Prueba 1");
        testingPoint.setDescription("Descripción del punto de prueba");
        testingPoint.setLocation("Lima, Perú");
        testingPoint.setActive(true);

        TestingPoint.Coordinates coordinates = new TestingPoint.Coordinates();
        coordinates.setLatitude(-12.0464);
        coordinates.setLongitude(-77.0428);
        testingPoint.setCoordinates(coordinates);

        createRequest = new TestingPointCreateRequest();
        createRequest.setName("Punto de Prueba 1");
        createRequest.setDescription("Descripción del punto de prueba");
        createRequest.setLocation("Lima, Perú");
        
        TestingPointCreateRequest.Coordinates reqCoordinates = new TestingPointCreateRequest.Coordinates();
        reqCoordinates.setLatitude(-12.0464);
        reqCoordinates.setLongitude(-77.0428);
        createRequest.setCoordinates(reqCoordinates);
    }

    @Test
    void shouldCreateTestingPointSuccessfully() {
        // Given
        when(testingPointRepository.save(any(TestingPoint.class))).thenReturn(Mono.just(testingPoint));

        // When
        Mono<TestingPointResponse> result = testingPointService.createTestingPoint(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getName().equals("Punto de Prueba 1") &&
                    response.getLocation().equals("Lima, Perú")
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllTestingPoints() {
        // Given
        when(testingPointRepository.findAll()).thenReturn(Flux.just(testingPoint));

        // When
        Flux<TestingPointResponse> result = testingPointService.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getName().equals("Punto de Prueba 1"))
                .verifyComplete();
    }

    @Test
    void shouldFindActiveTestingPoints() {
        // Given
        when(testingPointRepository.findByActiveTrue()).thenReturn(Flux.just(testingPoint));

        // When
        Flux<TestingPointResponse> result = testingPointService.findActiveTestingPoints();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getActive())
                .verifyComplete();
    }

    @Test
    void shouldFindTestingPointById() {
        // Given
        when(testingPointRepository.findById(anyString())).thenReturn(Mono.just(testingPoint));

        // When
        Mono<TestingPointResponse> result = testingPointService.findById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }
}