package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.TipoComprobante;

public class NotaCreditoTest {

    private NotaCredito notaCredito;
    private Factura factura;

    @BeforeEach
    void setUp() {
        notaCredito = new NotaCredito();
        // configuración de una factura básica para anular
        factura = new Factura();
        factura.setPrecioTotal(1000.0);
        factura.setEstado(EstadoFactura.VIGENTE); 
        factura.setTipo(TipoComprobante.B);
    }

    @Test
    @DisplayName("Debe registrar los atributos básicos (Motivo y Responsable)")
    void testAtributosBasicos() {
        notaCredito.setMotivoAnulacion("Error en facturación");
        notaCredito.setEmpleadoResponsable("Admin Test");
        notaCredito.setFecha(LocalDate.now());
        notaCredito.setTipo(TipoComprobante.B);

        assertEquals("Error en facturación", notaCredito.getMotivoAnulacion());
        assertEquals("Admin Test", notaCredito.getEmpleadoResponsable());
        assertNotNull(notaCredito.getFecha(), "La fecha no debería ser nula");
    }

    @Test
    @DisplayName("Debe vincular correctamente la Factura Anulada")
    void testVinculoConFactura() {
        notaCredito.setFacturaAnulada(factura);

        assertNotNull(notaCredito.getFacturaAnulada());
        assertEquals(1000.0, notaCredito.getFacturaAnulada().getPrecioTotal());
    }

    @Test
    @DisplayName("Debe permitir asignar un precio total negativo")
    void testPrecioNegativo() {
        
        double precioInvertido = factura.getPrecioTotal() * -1; // -1000.0
        notaCredito.setPrecioTotal(precioInvertido);

        assertEquals(-1000.0, notaCredito.getPrecioTotal(), 0.001);
        assertTrue(notaCredito.getPrecioTotal() < 0, "El precio total debería ser negativo");
    }

    @Test
    @DisplayName("Debe gestionar la lista de detalles de la nota")
    void testGestionDetallesNota() {
        
        DetalleNota detalleNota = new DetalleNota();
        detalleNota.setDescripcion("Devolución Servicio X");
        detalleNota.setPrecio(-500.0);
        detalleNota.setNotaCredito(notaCredito); 

        notaCredito.getDetallesNota().add(detalleNota);

        assertFalse(notaCredito.getDetallesNota().isEmpty());
        assertEquals(1, notaCredito.getDetallesNota().size());
        assertEquals("Devolución Servicio X", notaCredito.getDetallesNota().get(0).getDescripcion());
    }
}