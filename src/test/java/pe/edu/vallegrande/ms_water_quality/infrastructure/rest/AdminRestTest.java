package pe.edu.vallegrande.ms_water_quality.infrastructure.rest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.application.services.TestingPointService;
import pe.edu.vallegrande.ms_water_quality.application.services.QualityTestService;
import pe.edu.vallegrande.ms_water_quality.application.services.DailyRecordService;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.ResponseDto;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.enriched.TestingPointEnrichedResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.enriched.QualityTestEnrichedResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.enriched.DailyRecordEnrichedResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.rest.admin.AdminRest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminRestTest {

    @Mock
    private TestingPointService testingPointService;

    @Mock
    private QualityTestService qualityTestService;

    @Mock
    private DailyRecordService dailyRecordService;

    @InjectMocks
    private AdminRest adminRest;

    private TestingPointEnrichedResponse testingPointResponse;
    private QualityTestEnrichedResponse qualityTestResponse;
    private DailyRecordEnrichedResponse dailyRecordResponse;

    @BeforeEach
    void setUp() {
        testingPointResponse = TestingPointEnrichedResponse.builder()
                .id("1")
                .pointCode("TP001")
                .pointName("Test Point")
                .pointType("RESERVORIO")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();

        qualityTestResponse = QualityTestEnrichedResponse.builder()
                .id("1")
                .testCode("TEST001")
                .testType("WATER_QUALITY")
                .status("COMPLETED")
                .createdAt(LocalDateTime.now())
                .build();

        dailyRecordResponse = DailyRecordEnrichedResponse.builder()
                .id("1")
                .recordCode("REC001")
                .recordType("CLORO")
                .acceptable(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldGetAllTestingPoints() {
        // Given
        when(testingPointService.getAll()).thenReturn(Flux.just(testingPointResponse));

        // When
        Mono<ResponseDto<List<TestingPointEnrichedResponse>>> result = adminRest.getAllTestingPoints(null);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.isStatus() && 
                    response.getData().size() == 1
                )
                .verifyComplete();
    }

    @Test
    void shouldGetTestingPointById() {
        // Given
        when(testingPointService.getById("1")).thenReturn(Mono.just(testingPointResponse));

        // When
        Mono<ResponseDto<TestingPointEnrichedResponse>> result = adminRest.getTestingPointById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.isStatus() && 
                    response.getData().getId().equals("1")
                )
                .verifyComplete();
    }

    @Test
    void shouldGetAllQualityTests() {
        // Given
        when(qualityTestService.getAll()).thenReturn(Flux.just(qualityTestResponse));

        // When
        Mono<ResponseDto<List<QualityTestEnrichedResponse>>> result = adminRest.getAllTests();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.isStatus() && 
                    response.getData().size() == 1
                )
                .verifyComplete();
    }
}