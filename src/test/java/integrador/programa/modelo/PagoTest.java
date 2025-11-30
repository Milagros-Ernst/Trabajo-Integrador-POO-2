package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.MetodoPago;

public class PagoTest {

    private Pago pago;

    @BeforeEach
    void setUp() {
        pago = new Pago();
        pago.setImporte(1000.0);
        pago.setMetodoPago(MetodoPago.TRANSFERENCIA); // o el valor que corresponda en tu enum
        pago.setEmpleadoResponsable("Operador Caja 1");
        // fechaPago tiene valor por defecto LocalDate.now()
    }

    @Test
    @DisplayName("Un pago debe registrar los datos obligatorios (HU 12 / HU 13)")
    void testCamposObligatoriosPago() {
        assertNotNull(pago.getImporte(), "El importe no debe ser nulo");
        assertEquals(1000.0, pago.getImporte());

        assertNotNull(pago.getMetodoPago(), "El método de pago no debe ser nulo");
        assertEquals(MetodoPago.TRANSFERENCIA, pago.getMetodoPago());

        assertNotNull(pago.getEmpleadoResponsable(), "El empleado responsable no debe ser nulo");
        assertEquals("Operador Caja 1", pago.getEmpleadoResponsable());

        assertNotNull(pago.getFechaPago(), "La fecha de pago no debe ser nula");
        assertEquals(LocalDate.now(), pago.getFechaPago(),
                "La fecha de pago por defecto debería ser la fecha actual");
    }

    @Test
    @DisplayName("Debe devolver la descripción del método de pago según el enum (HU 12 / HU 13)")
    void testGetDescripcionMetodoPago() {
        MetodoPago metodo = MetodoPago.TRANSFERENCIA; // ajustá al enum real
        pago.setMetodoPago(metodo);

        String descripcionEsperada = metodo.getDescripcion();
        assertEquals(descripcionEsperada, pago.getDescripcionMetodoPago(),
                "La descripción del método de pago debe provenir del enum");
    }

    @Test
    @DisplayName("Si no hay método de pago debe devolver descripción vacía")
    void testGetDescripcionMetodoPagoSinMetodo() {
        pago.setMetodoPago(null);
        assertEquals("", pago.getDescripcionMetodoPago(),
                "Sin método de pago la descripción debe ser cadena vacía");
    }

    @Test
    @DisplayName("Debe poder asociar un recibo al pago (relación 1:1) (HU 12 / HU 13)")
    void testAsociarReciboAPago() {
        Recibo recibo = new Recibo();
        pago.setRecibo(recibo);

        assertNotNull(pago.getRecibo(), "El recibo asociado no debe ser nulo");
        assertEquals(recibo, pago.getRecibo(), "El pago debe mantener la referencia al recibo asociado");
    }
}
