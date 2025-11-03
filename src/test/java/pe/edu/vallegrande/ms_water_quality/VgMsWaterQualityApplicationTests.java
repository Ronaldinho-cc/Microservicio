package pe.edu.vallegrande.ms_water_quality;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class VgMsWaterQualityApplicationTests {

    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring se puede cargar
        // sin inicializar el servidor web completo
    }

    @Test
    void applicationCanInstantiate() {
        // Prueba básica para verificar que la clase principal se puede instanciar
        VgMsWaterQuality app = new VgMsWaterQuality();
        assert app != null;
    }

    @Test
    void basicAssertionsWork() {
        // Prueba básica para verificar que JUnit funciona correctamente
        String expected = "test";
        String actual = "test";
        assert expected.equals(actual);
        
        int number = 42;
        assert number > 0;
        assert number < 100;
    }
}