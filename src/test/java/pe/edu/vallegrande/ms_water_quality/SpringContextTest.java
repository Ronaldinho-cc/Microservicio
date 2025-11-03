package pe.edu.vallegrande.ms_water_quality;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Spring context test that can be run separately when needed.
 * This test is excluded by default to avoid MongoDB dependency issues.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource(locations = "classpath:application-test.properties")
class SpringContextTest {

    @Test
    void contextLoads() {
        // Esta prueba verifica que el contexto de Spring se puede cargar
        // sin inicializar el servidor web completo
        // Solo se ejecuta cuando se necesita probar la configuración de Spring
    }
}