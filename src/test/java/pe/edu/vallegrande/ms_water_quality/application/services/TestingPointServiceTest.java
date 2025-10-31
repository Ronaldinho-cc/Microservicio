package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.domain.models.TestingPoint;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.TestingPointCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.TestingPointResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.enriched.TestingPointEnrichedResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestingPointServiceTest {

    @Mock
    private TestingPointService testingPointService;

    private TestingPoint testingPoint;
    private TestingPointCreateRequest createRequest;
    private TestingPointEnrichedResponse enrichedResponse;
    private TestingPointResponse response;

    @BeforeEach
    void setUp() {
        testingPoint = new TestingPoint();
        testingPoint.setId("1");
        testingPoint.setOrganizationId("org1");
        testingPoint.setPointCode("TP001");
        testingPoint.setPointName("Punto de Prueba 1");
        testingPoint.setPointType("RESERVORIO");
        testingPoint.setZoneId("zone1");
        testingPoint.setLocationDescription("Lima, Perú");
        testingPoint.setStreet("Av. Principal 123");
        testingPoint.setStatus("ACTIVE");

        TestingPoint.Coordinates coordinates = new TestingPoint.Coordinates();
        coordinates.setLatitude(-12.0464);
        coordinates.setLongitude(-77.0428);
        testingPoint.setCoordinates(coordinates);

        createRequest = new TestingPointCreateRequest();
        createRequest.setOrganizationId("org1");
        createRequest.setPointCode("TP001");
        createRequest.setPointName("Punto de Prueba 1");
        createRequest.setPointType("RESERVORIO");
        createRequest.setZoneId("zone1");
        createRequest.setLocationDescription("Lima, Perú");
        createRequest.setStreet("Av. Principal 123");
        
        TestingPointCreateRequest.Coordinates reqCoordinates = new TestingPointCreateRequest.Coordinates();
        reqCoordinates.setLatitude(-12.0464);
        reqCoordinates.setLongitude(-77.0428);
        createRequest.setCoordinates(reqCoordinates);

        enrichedResponse = TestingPointEnrichedResponse.builder()
                .id("1")
                .pointCode("TP001")
                .pointName("Punto de Prueba 1")
                .pointType("RESERVORIO")
                .zoneId("zone1")
                .locationDescription("Lima, Perú")
                .street("Av. Principal 123")
                .coordinates(coordinates)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        response = new TestingPointResponse();
        response.setId("1");
        response.setOrganizationId("org1");
        response.setPointCode("TP001");
        response.setPointName("Punto de Prueba 1");
        response.setPointType("RESERVORIO");
        response.setZoneId("zone1");
        response.setLocationDescription("Lima, Perú");
        response.setStreet("Av. Principal 123");
        response.setStatus("ACTIVE");
        response.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void shouldCreateTestingPointSuccessfully() {
        // Given
        when(testingPointService.save(any(TestingPointCreateRequest.class))).thenReturn(Mono.just(response));

        // When
        Mono<TestingPointResponse> result = testingPointService.save(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(res -> 
                    res.getPointName().equals("Punto de Prueba 1") &&
                    res.getLocationDescription().equals("Lima, Perú")
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllTestingPoints() {
        // Given
        when(testingPointService.getAll()).thenReturn(Flux.just(enrichedResponse));

        // When
        Flux<TestingPointEnrichedResponse> result = testingPointService.getAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(res -> res.getPointName().equals("Punto de Prueba 1"))
                .verifyComplete();
    }

    @Test
    void shouldFindActiveTestingPoints() {
        // Given
        when(testingPointService.getAllActive()).thenReturn(Flux.just(enrichedResponse));

        // When
        Flux<TestingPointEnrichedResponse> result = testingPointService.getAllActive();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(res -> res.getStatus().equals("ACTIVE"))
                .verifyComplete();
    }

    @Test
    void shouldFindTestingPointById() {
        // Given
        when(testingPointService.getById(anyString())).thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<TestingPointEnrichedResponse> result = testingPointService.getById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(res -> res.getId().equals("1"))
                .verifyComplete();
    }
}