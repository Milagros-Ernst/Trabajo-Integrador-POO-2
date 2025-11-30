package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.TipoDocumento;

public class CuentaTest {

    /**
     * Subclase concreta mínima para poder instanciar Cuenta en los tests.
     */
    private static class CuentaConcreta extends Cuenta {
        // No agrega nada, solo nos permite instanciar Cuenta
    }

    @Test
    @DisplayName("La cuenta debe iniciar en estado ACTIVA (HU 01)")
    void testEstadoCuentaPorDefectoActiva() {
        CuentaConcreta cuenta = new CuentaConcreta();
        assertEquals(EstadoCuenta.ACTIVA, cuenta.getEstadoCuenta(),
                "El estado por defecto de la cuenta debe ser ACTIVA");
    }

    @Test
    @DisplayName("Debe permitir activar y desactivar la cuenta (HU 02)")
    void testActivarYDesactivarCuenta() {
        CuentaConcreta cuenta = new CuentaConcreta();
        cuenta.setNombre("Ana");
        cuenta.setApellido("López");

        // Por defecto activa
        assertTrue(cuenta.estaActiva());

        // Desactivar
        cuenta.desactivar();
        assertEquals(EstadoCuenta.INACTIVA, cuenta.getEstadoCuenta());
        assertFalse(cuenta.estaActiva());

        // Activar nuevamente
        cuenta.activar();
        assertEquals(EstadoCuenta.ACTIVA, cuenta.getEstadoCuenta());
        assertTrue(cuenta.estaActiva());
    }

    @Test
    @DisplayName("Debe validar correctamente el número de documento para DNI (HU 01)")
    void testValidarDocumentoDNI() {
        CuentaConcreta cuenta = new CuentaConcreta();
        cuenta.setTipoDocumento(TipoDocumento.DNI);

        // DNI válido: 7 u 8 dígitos
        cuenta.setNumeroDocumento("12345678");
        assertTrue(cuenta.validarDocumento(), "Un DNI de 8 dígitos debe ser válido");

        // DNI inválidos
        cuenta.setNumeroDocumento("123456"); // muy corto
        assertFalse(cuenta.validarDocumento(), "Un DNI de 6 dígitos no es válido");

        cuenta.setNumeroDocumento("123456789"); // muy largo
        assertFalse(cuenta.validarDocumento(), "Un DNI de 9 dígitos no es válido");
    }

    @Test
    @DisplayName("Debe validar correctamente el número de documento para CUIT y CUIL (HU 01)")
    void testValidarDocumentoCUITyCUIL() {
        CuentaConcreta cuenta = new CuentaConcreta();

        // CUIT válido (11 dígitos)
        cuenta.setTipoDocumento(TipoDocumento.CUIT);
        cuenta.setNumeroDocumento("20123456789");
        assertTrue(cuenta.validarDocumento(), "Un CUIT de 11 dígitos debe ser válido");

        // CUIL válido (11 dígitos)
        cuenta.setTipoDocumento(TipoDocumento.CUIL);
        cuenta.setNumeroDocumento("27123456789");
        assertTrue(cuenta.validarDocumento(), "Un CUIL de 11 dígitos debe ser válido");

        // Menos de 11 dígitos => inválido
        cuenta.setNumeroDocumento("1234567890");
        assertFalse(cuenta.validarDocumento(), "Un CUIT/CUIL de 10 dígitos no es válido");
    }

    @Test
    @DisplayName("La validación de documento debe fallar si faltan datos (HU 01)")
    void testValidarDocumentoConDatosIncompletos() {
        CuentaConcreta cuenta = new CuentaConcreta();

        // Sin tipo de documento
        cuenta.setNumeroDocumento("12345678");
        assertFalse(cuenta.validarDocumento(), "Sin tipo de documento no debería ser válido");

        // Sin número de documento
        cuenta.setTipoDocumento(TipoDocumento.DNI);
        cuenta.setNumeroDocumento(null);
        assertFalse(cuenta.validarDocumento(), "Sin número de documento no debería ser válido");
    }

    @Test
    @DisplayName("El nombre completo combina nombre y apellido (HU 01 / HU 02)")
    void testGetNombreCompleto() {
        CuentaConcreta cuenta = new CuentaConcreta();
        cuenta.setNombre("Carlos");
        cuenta.setApellido("Gómez");

        assertEquals("Carlos Gómez", cuenta.getNombreCompleto());
    }

    @Test
    @DisplayName("Dos cuentas son iguales si tienen mismo tipo y número de documento")
    void testEqualsYHashCodePorDocumento() {
        CuentaConcreta c1 = new CuentaConcreta();
        c1.setTipoDocumento(TipoDocumento.DNI);
        c1.setNumeroDocumento("12345678");

        CuentaConcreta c2 = new CuentaConcreta();
        c2.setTipoDocumento(TipoDocumento.DNI);
        c2.setNumeroDocumento("12345678");

        CuentaConcreta c3 = new CuentaConcreta();
        c3.setTipoDocumento(TipoDocumento.DNI);
        c3.setNumeroDocumento("87654321");

        assertEquals(c1, c2, "Cuentas con mismo tipo y número de documento deben ser iguales");
        assertEquals(c1.hashCode(), c2.hashCode(), "El hashCode también debe coincidir");

        assertNotEquals(c1, c3, "Cuentas con distinto número de documento no deben ser iguales");
    }
}
