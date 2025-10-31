package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.domain.models.DailyRecord;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.DailyRecordCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.enriched.DailyRecordEnrichedResponse;
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
class DailyRecordServiceTest {

    @Mock
    private DailyRecordService dailyRecordService;

    private DailyRecord dailyRecord;
    private DailyRecordCreateRequest createRequest;
    private DailyRecordEnrichedResponse enrichedResponse;

    @BeforeEach
    void setUp() {
        List<String> testingPointIds = Arrays.asList("point1", "point2");
        
        dailyRecord = new DailyRecord();
        dailyRecord.setId("1");
        dailyRecord.setOrganizationId("org1");
        dailyRecord.setRecordCode("REC001");
        dailyRecord.setTestingPointIds(testingPointIds);
        dailyRecord.setRecordDate(LocalDateTime.now());
        dailyRecord.setLevel(24.5);
        dailyRecord.setAcceptable(true);
        dailyRecord.setActionRequired(false);
        dailyRecord.setRecordedByUserId("user1");
        dailyRecord.setObservations("Test observations");
        dailyRecord.setAmount(100.0);
        dailyRecord.setRecordType("CLORO");
        dailyRecord.setCreatedAt(LocalDateTime.now());

        createRequest = new DailyRecordCreateRequest();
        createRequest.setOrganization("org1");
        createRequest.setRecordCode("REC001");
        createRequest.setTestingPoints(testingPointIds);
        createRequest.setRecordDate(LocalDateTime.now());
        createRequest.setLevel(24.5);
        createRequest.setAcceptable(true);
        createRequest.setActionRequired(false);
        createRequest.setRecordedByUser("user1");
        createRequest.setObservations("Test observations");
        createRequest.setAmount(100.0);
        createRequest.setRecordType("CLORO");

        enrichedResponse = DailyRecordEnrichedResponse.builder()
                .id("1")
                .recordCode("REC001")
                .recordDate(LocalDateTime.now())
                .level(24.5)
                .acceptable(true)
                .actionRequired(false)
                .observations("Test observations")
                .amount(100.0)
                .recordType("CLORO")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCreateDailyRecordSuccessfully() {
        // Given
        when(dailyRecordService.save(any(DailyRecordCreateRequest.class))).thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<DailyRecordEnrichedResponse> result = dailyRecordService.save(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getRecordCode().equals("REC001") &&
                    response.getLevel().equals(24.5) &&
                    response.isAcceptable()
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllDailyRecords() {
        // Given
        when(dailyRecordService.getAll()).thenReturn(Flux.just(enrichedResponse));

        // When
        Flux<DailyRecordEnrichedResponse> result = dailyRecordService.getAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getRecordCode().equals("REC001"))
                .verifyComplete();
    }

    @Test
    void shouldFindDailyRecordsByOrganization() {
        // Given
        when(dailyRecordService.getAllByOrganization(anyString())).thenReturn(Flux.just(enrichedResponse));

        // When
        Flux<DailyRecordEnrichedResponse> result = dailyRecordService.getAllByOrganization("org1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getRecordCode().equals("REC001"))
                .verifyComplete();
    }

    @Test
    void shouldFindDailyRecordById() {
        // Given
        when(dailyRecordService.getById(anyString())).thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<DailyRecordEnrichedResponse> result = dailyRecordService.getById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldUpdateDailyRecord() {
        // Given
        when(dailyRecordService.update(anyString(), any(DailyRecordCreateRequest.class)))
                .thenReturn(Mono.just(enrichedResponse));

        // When
        Mono<DailyRecordEnrichedResponse> result = dailyRecordService.update("1", createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }
}