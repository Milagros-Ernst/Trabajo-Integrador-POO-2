package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ReciboTest {

    private Recibo recibo;
    private Cliente cliente;
    private Factura factura1;
    private Factura factura2;

    @BeforeEach
    void setUp() {
        // Cliente básico
        cliente = new Cliente();
        cliente.setNombre("Carlos");
        cliente.setApellido("Gómez");

        // Facturas de prueba
        factura1 = new Factura();
        factura1.setPrecioTotal(1000.0);

        factura2 = new Factura();
        factura2.setPrecioTotal(500.0);

        // Recibo vacío inicial
        recibo = new Recibo();
        recibo.setNroRecibo(1L);
        recibo.setCliente(cliente);
        recibo.setImporteTotal(0.0); // lo iremos simulando a mano en los tests
    }

    @Test
    @DisplayName("Debe asignar y obtener los atributos básicos del Recibo")
    void testAtributosBasicosRecibo() {
        assertEquals(1L, recibo.getNroRecibo());
        assertEquals(cliente, recibo.getCliente());
        assertNotNull(recibo.getDetalles(), "La lista de detalles no debería ser nula");
        assertEquals(0.0, recibo.getImporteTotal(), 0.001);
    }

    @Test
    @DisplayName("Debe mantener la relación bidireccional al agregar un detalle")
    void testAgregarDetalleMantieneBidireccionalidad() {
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setFactura(factura1);
        detalle.setImporteAplicado(300.0);

        recibo.agregarDetalle(detalle);

        List<DetalleRecibo> detallesRecibo = recibo.getDetalles();

        assertEquals(1, detallesRecibo.size(), "Debería haber 1 detalle en el recibo");
        assertSame(detalle, detallesRecibo.get(0), "El detalle agregado debe estar en la lista");
        assertSame(recibo, detalle.getRecibo(), "El detalle debe tener seteado el recibo en la relación inversa");
    }

    @Test
    @DisplayName("Debe quitar el detalle y romper la relación inversa")
    void testQuitarDetalleRompeRelacion() {
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setFactura(factura1);
        detalle.setImporteAplicado(300.0);

        // primero lo agregamos
        recibo.agregarDetalle(detalle);
        assertEquals(1, recibo.getDetalles().size());

        // ahora lo quitamos
        recibo.quitarDetalle(detalle);

        assertTrue(recibo.getDetalles().isEmpty(), "La lista de detalles debería quedar vacía");
        assertNull(detalle.getRecibo(), "El detalle debería dejar de apuntar al recibo");
    }

    @Test
    @DisplayName("Debe permitir agregar múltiples detalles para distintas facturas")
    void testAgregarMultiplesDetalles() {
        DetalleRecibo d1 = new DetalleRecibo();
        d1.setFactura(factura1);
        d1.setImporteAplicado(400.0);

        DetalleRecibo d2 = new DetalleRecibo();
        d2.setFactura(factura2);
        d2.setImporteAplicado(200.0);

        recibo.agregarDetalle(d1);
        recibo.agregarDetalle(d2);

        assertEquals(2, recibo.getDetalles().size());
        assertTrue(recibo.getDetalles().contains(d1));
        assertTrue(recibo.getDetalles().contains(d2));

        // Simulación manual del total del recibo según lo aplicado
        double totalEsperado = 400.0 + 200.0;
        recibo.setImporteTotal(totalEsperado);

        assertEquals(totalEsperado, recibo.getImporteTotal(), 0.001, 
            "El importe total debería coincidir con la suma de los importes aplicados");
    }
}
