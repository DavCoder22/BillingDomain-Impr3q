package com.example.payment;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")
class PaymentServiceApplicationTest {

    @Test
    void contextLoads() {
        // Verifica que el contexto de la aplicación se cargue correctamente
        assertTrue(true, "El contexto de la aplicación debería cargarse correctamente");
    }

    @Test
    void main_WhenCalled_ShouldStartApplication() {
        // Verifica que el método main pueda ejecutarse sin excepciones
        String[] args = {};
        assertDoesNotThrow(() -> PaymentServiceApplication.main(args));
    }
}
