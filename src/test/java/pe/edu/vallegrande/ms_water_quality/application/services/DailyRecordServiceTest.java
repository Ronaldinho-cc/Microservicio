package pe.edu.vallegrande.ms_water_quality.application.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.edu.vallegrande.ms_water_quality.application.services.impl.DailyRecordServiceImpl;
import pe.edu.vallegrande.ms_water_quality.domain.models.DailyRecord;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.request.DailyRecordCreateRequest;
import pe.edu.vallegrande.ms_water_quality.infrastructure.dto.response.DailyRecordResponse;
import pe.edu.vallegrande.ms_water_quality.infrastructure.repository.DailyRecordRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailyRecordServiceTest {

    @Mock
    private DailyRecordRepository dailyRecordRepository;

    @InjectMocks
    private DailyRecordServiceImpl dailyRecordService;

    private DailyRecord dailyRecord;
    private DailyRecordCreateRequest createRequest;

    @BeforeEach
    void setUp() {
        dailyRecord = new DailyRecord();
        dailyRecord.setId("1");
        dailyRecord.setTestingPointId("point1");
        dailyRecord.setUserId("user1");
        dailyRecord.setRecordDate(LocalDate.now());
        dailyRecord.setAverageTemperature(24.5);
        dailyRecord.setAveragePh(7.1);
        dailyRecord.setAverageOxygen(8.2);
        dailyRecord.setAverageTurbidity(1.8);
        dailyRecord.setTestsCount(5);
        dailyRecord.setQualityScore(88.5);
        dailyRecord.setCreatedAt(LocalDateTime.now());

        createRequest = new DailyRecordCreateRequest();
        createRequest.setTestingPointId("point1");
        createRequest.setUserId("user1");
        createRequest.setRecordDate(LocalDate.now());
        createRequest.setAverageTemperature(24.5);
        createRequest.setAveragePh(7.1);
        createRequest.setAverageOxygen(8.2);
        createRequest.setAverageTurbidity(1.8);
        createRequest.setTestsCount(5);
        createRequest.setQualityScore(88.5);
    }

    @Test
    void shouldCreateDailyRecordSuccessfully() {
        // Given
        when(dailyRecordRepository.save(any(DailyRecord.class))).thenReturn(Mono.just(dailyRecord));

        // When
        Mono<DailyRecordResponse> result = dailyRecordService.createDailyRecord(createRequest);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> 
                    response.getTestingPointId().equals("point1") &&
                    response.getTestsCount().equals(5) &&
                    response.getQualityScore().equals(88.5)
                )
                .verifyComplete();
    }

    @Test
    void shouldFindAllDailyRecords() {
        // Given
        when(dailyRecordRepository.findAll()).thenReturn(Flux.just(dailyRecord));

        // When
        Flux<DailyRecordResponse> result = dailyRecordService.findAll();

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestingPointId().equals("point1"))
                .verifyComplete();
    }

    @Test
    void shouldFindDailyRecordsByTestingPoint() {
        // Given
        when(dailyRecordRepository.findByTestingPointId(anyString())).thenReturn(Flux.just(dailyRecord));

        // When
        Flux<DailyRecordResponse> result = dailyRecordService.findByTestingPointId("point1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestingPointId().equals("point1"))
                .verifyComplete();
    }

    @Test
    void shouldFindDailyRecordById() {
        // Given
        when(dailyRecordRepository.findById(anyString())).thenReturn(Mono.just(dailyRecord));

        // When
        Mono<DailyRecordResponse> result = dailyRecordService.findById("1");

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getId().equals("1"))
                .verifyComplete();
    }

    @Test
    void shouldFindDailyRecordsByDateRange() {
        // Given
        LocalDate startDate = LocalDate.now().minusDays(7);
        LocalDate endDate = LocalDate.now();
        when(dailyRecordRepository.findByRecordDateBetween(startDate, endDate))
                .thenReturn(Flux.just(dailyRecord));

        // When
        Flux<DailyRecordResponse> result = dailyRecordService.findByDateRange(startDate, endDate);

        // Then
        StepVerifier.create(result)
                .expectNextMatches(response -> response.getTestingPointId().equals("point1"))
                .verifyComplete();
    }
}