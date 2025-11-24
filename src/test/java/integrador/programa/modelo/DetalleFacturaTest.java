package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.TipoIVA;

public class DetalleFacturaTest {

    private DetalleFactura detalle;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        detalle = new DetalleFactura();
        
        servicio = new Servicio();
        servicio.setNombre("Servicio Test");
        servicio.setTipoIva(TipoIVA.IVA_21); 
        detalle.setServicio(servicio);
        detalle.setPrecio(100); 
    }

    @Test
    @DisplayName("Debe calcular el monto del IVA correctamente (21%)")
    void testCalcularMontoIva21() {

        double ivaCalculado = detalle.getMontoIvaCalculado();
        assertEquals(21.0, ivaCalculado, 0.001, "El IVA de 100 al 21% debería ser 21.0");
    }

    @Test
    @DisplayName("Debe calcular el monto del IVA correctamente (10.5%)")
    void testCalcularMontoIva105() {
        servicio.setTipoIva(TipoIVA.IVA_105);
        detalle.setPrecio(200); 
        double ivaCalculado = detalle.getMontoIvaCalculado();
        assertEquals(21.0, ivaCalculado, 0.001, "El IVA de 200 al 10.5% debería ser 21.0");
    }

    @Test
    @DisplayName("Debe calcular el precio final sumando el IVA")
    void testCalcularPrecioConIva() {
        double precioFinal = detalle.getPrecioConIvaCalculado();
        assertEquals(121.0, precioFinal, 0.001);
    }

    // iba a armar uno de precio positivo pero eso lo maneja el @Positive del detalle así que funciona.
}