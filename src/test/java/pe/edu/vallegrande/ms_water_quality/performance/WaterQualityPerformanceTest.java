package pe.edu.vallegrande.ms_water_quality.performance;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class WaterQualityPerformanceTest {

    @Test
    void shouldProcessWaterQualityCalculationQuickly() {
        // Given
        Instant start = Instant.now();
        
        // When
        for (int i = 0; i < 1000; i++) {
            calculateWaterQualityScore(7.0, 8.0, 20.0, 1.0);
        }
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        // Then
        assertTrue(duration.toMillis() < 1000, "1000 calculations should take less than 1 second");
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 50, 100, 500, 1000})
    void shouldHandleDifferentVolumeLoads(int numberOfCalculations) {
        // Given
        Instant start = Instant.now();
        
        // When
        for (int i = 0; i < numberOfCalculations; i++) {
            double ph = 6.5 + (Math.random() * 2.0); // 6.5 - 8.5
            double oxygen = 5.0 + (Math.random() * 5.0); // 5.0 - 10.0
            double temperature = 15.0 + (Math.random() * 15.0); // 15.0 - 30.0
            double turbidity = Math.random() * 5.0; // 0.0 - 5.0
            
            calculateWaterQualityScore(ph, oxygen, temperature, turbidity);
        }
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        
        // Then
        long maxExpectedTime = numberOfCalculations / 10; // 10ms per calculation max
        assertTrue(duration.toMillis() < maxExpectedTime, 
            numberOfCalculations + " calculations took " + duration.toMillis() + "ms, expected < " + maxExpectedTime + "ms");
    }

    @ParameterizedTest
    @CsvSource({
        "1, 100",
        "5, 500", 
        "10, 1000",
        "20, 2000"
    })
    void shouldHandleConcurrentCalculations(int threads, int calculationsPerThread) throws Exception {
        // Given
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        Instant start = Instant.now();

        // When
        for (int t = 0; t < threads; t++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                for (int i = 0; i < calculationsPerThread; i++) {
                    calculateWaterQualityScore(7.0, 8.0, 20.0, 1.0);
                }
            }, executor);
            futures.add(future);
        }

        // Wait for all threads to complete
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        Instant end = Instant.now();
        Duration duration = Duration.between(start, end);
        executor.shutdown();

        // Then
        long totalCalculations = (long) threads * calculationsPerThread;
        long maxExpectedTime = totalCalculations / 5; // 5ms per calculation max in concurrent scenario
        assertTrue(duration.toMillis() < maxExpectedTime, 
            "Concurrent execution of " + totalCalculations + " calculations took " + duration.toMillis() + "ms");
    }

    @Test
    void shouldValidateMemoryUsageForLargeDatasets() {
        // Given
        Runtime runtime = Runtime.getRuntime();
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        
        // When
        List<Double> results = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            double result = calculateWaterQualityScore(7.0, 8.0, 20.0, 1.0);
            results.add(result);
        }
        
        long finalMemory = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = finalMemory - initialMemory;
        
        // Then
        assertTrue(memoryUsed < 50 * 1024 * 1024, "Memory usage should be less than 50MB"); // 50MB limit
        assertEquals(10000, results.size());
    }

    private double calculateWaterQualityScore(double ph, double oxygen, double temperature, double turbidity) {
        // Simulación del cálculo de calidad del agua
        double phScore = (ph >= 6.5 && ph <= 8.5) ? 100 - Math.abs(7.0 - ph) * 10 : 0;
        double oxygenScore = oxygen >= 8.0 ? 100 : oxygen >= 5.0 ? oxygen * 12.5 : 0;
        double temperatureScore = (temperature >= 15 && temperature <= 25) ? 100 : 50;
        double turbidityScore = turbidity <= 1.0 ? 100 : turbidity <= 5.0 ? 100 - (turbidity - 1) * 20 : 20;
        
        return (phScore + oxygenScore + temperatureScore + turbidityScore) / 4;
    }
}