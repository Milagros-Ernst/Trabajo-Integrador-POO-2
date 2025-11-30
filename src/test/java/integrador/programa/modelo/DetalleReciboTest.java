package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class DetalleReciboTest {

    @Test
    @DisplayName("Debe asignar y recuperar correctamente los atributos básicos")
    void testAtributosBasicos() {
        Recibo recibo = new Recibo();
        Factura factura = new Factura();

        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setIdDetalleRecibo(10L);
        detalle.setRecibo(recibo);
        detalle.setFactura(factura);
        detalle.setImporteAplicado(250.0);

        assertEquals(10L, detalle.getIdDetalleRecibo());
        assertEquals(recibo, detalle.getRecibo());
        assertEquals(factura, detalle.getFactura());
        assertEquals(250.0, detalle.getImporteAplicado(), 0.001);
    }

    @Test
    @DisplayName("Múltiples detalles pueden referenciar la misma factura (pagos parciales)")
    void testMultiplesDetallesMismaFactura() {
        Factura factura = new Factura();
        factura.setPrecioTotal(1000.0);

        Recibo recibo1 = new Recibo();
        Recibo recibo2 = new Recibo();

        DetalleRecibo detalle1 = new DetalleRecibo();
        detalle1.setFactura(factura);
        detalle1.setRecibo(recibo1);
        detalle1.setImporteAplicado(400.0);

        DetalleRecibo detalle2 = new DetalleRecibo();
        detalle2.setFactura(factura);
        detalle2.setRecibo(recibo2);
        detalle2.setImporteAplicado(600.0);

        // Ambos detalles apuntan a la misma factura
        assertSame(factura, detalle1.getFactura());
        assertSame(factura, detalle2.getFactura());

        // El total "aplicado" a esa factura, sumando ambos recibos, es el total de la factura
        double totalAplicado = detalle1.getImporteAplicado() + detalle2.getImporteAplicado();
        assertEquals(1000.0, totalAplicado, 0.001,
            "La suma de importes aplicados en los detalles debería ser igual al total de la factura");
    }
}
