package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LogFacturacionMasTest {

    @Test
    @DisplayName("Los atributos deben asignarse correctamente")
    void testAtributos() {
        int periodoEsperado = 5; 
        int cantidadEsperada = 150;
        String serviciosEsperados = "Internet 100MB, Cable TV";
        String responsableEsperado = "Juan Perez";

        LogFacturacionMasiva log = new LogFacturacionMasiva(
            periodoEsperado, 
            cantidadEsperada, 
            serviciosEsperados, 
            responsableEsperado
        );

        assertEquals(periodoEsperado, log.getPeriodo());
        assertEquals(cantidadEsperada, log.getCantidadFacturas());
        assertEquals(serviciosEsperados, log.getServiciosIncluidos());
        assertEquals(responsableEsperado, log.getEmpleadoResponsable());
        
        assertNotNull(log.getFechaEjecucion(), "La fecha de ejecución no debería ser nula");
        assertEquals(LocalDate.now(), log.getFechaEjecucion(), "La fecha debería ser la de hoy");
    }
}