package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


public class DetalleNotaTest {

    private DetalleNota detalleNota;

    @BeforeEach
    void setUp() {
        detalleNota = new DetalleNota();
        detalleNota.setDescripcion("Devolución por error");
        detalleNota.setPrecio(150.0); 

        // simulamos las relaciones obligatorias
        NotaCredito notaDummy = new NotaCredito();
        detalleNota.setNotaCredito(notaDummy);
        
        DetalleFactura detalleFacturaDummy = new DetalleFactura();
        detalleNota.setDetalleFactura(detalleFacturaDummy);
    }


    @Test
    @DisplayName("Debe permitir asignar y recuperar atributos correctamente (Getters/Setters)")
    void testManejoDeAtributos() {
        DetalleNota detalle = new DetalleNota();
        String descripcionEsperada = "Reintegro por falla";
        double precioEsperado = 1500.0;

        detalle.setDescripcion(descripcionEsperada);
        detalle.setPrecio(precioEsperado);

        assertEquals(descripcionEsperada, detalle.getDescripcion());
        assertEquals(precioEsperado, detalle.getPrecio(), 0.001);
    }
}