package pe.edu.vallegrande.ms_water_quality;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class VgMsWaterQualityApplicationTests {

    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring se carga correctamente
    }

    @Test
    void applicationStarts() {
        // Esta prueba verifica que la aplicación puede iniciarse
        assert true;
    }
}