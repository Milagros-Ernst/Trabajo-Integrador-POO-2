package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.CondicionIVA;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.TipoDocumento;

public class ClienteTest {

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        // Campos heredados de Cuenta (HU 01 - Alta Cliente)
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setTipoDocumento(TipoDocumento.DNI);
        cliente.setNumeroDocumento("12345678");

        // Campos propios de Cliente (HU 01 - Alta Cliente)
        cliente.setCondIVA(CondicionIVA.CONSUMIDOR_FINAL);
        cliente.setDireccion("Calle Falsa 123");
        cliente.setTelefono("3755-123456");
        cliente.setMail("juan.perez@example.com");
        cliente.setDireccionFiscal("Calle Fiscal 456");
    }

    @Test
    @DisplayName("Al dar de alta un cliente su cuenta debe iniciar ACTIVA (HU 01)")
    void testEstadoCuentaPorDefectoEsActiva() {
        Cliente nuevoCliente = new Cliente();
        assertEquals(EstadoCuenta.ACTIVA, nuevoCliente.getEstadoCuenta(),
                "La cuenta de un cliente nuevo debe iniciar en estado ACTIVA");
    }

    @Test
    @DisplayName("Debe permitir registrar correctamente los datos obligatorios del cliente (HU 01)")
    void testRegistrarDatosBasicosCliente() {
        assertEquals("Juan", cliente.getNombre());
        assertEquals("Pérez", cliente.getApellido());
        assertEquals(TipoDocumento.DNI, cliente.getTipoDocumento());
        assertEquals("12345678", cliente.getNumeroDocumento());

        assertEquals(CondicionIVA.CONSUMIDOR_FINAL, cliente.getCondIVA());
        assertEquals("Calle Falsa 123", cliente.getDireccion());
        assertEquals("3755-123456", cliente.getTelefono());
        assertEquals("juan.perez@example.com", cliente.getMail());
        assertEquals("Calle Fiscal 456", cliente.getDireccionFiscal());
    }

    @Test
    @DisplayName("El nombre completo del cliente debe combinar nombre y apellido (HU 01 / HU 02)")
    void testNombreCompletoCliente() {
        String nombreCompleto = cliente.getNombreCompleto();
        assertEquals("Juan Pérez", nombreCompleto);
    }

    @Test
    @DisplayName("Debe poder cambiar el estado de la cuenta a ACTIVA/INACTIVA (HU 02)")
    void testCambiarEstadoCuenta() {
        // Por defecto ACTIVA
        assertTrue(cliente.estaActiva(), "Al inicio la cuenta debería estar activa");

        // Desactivar (HU 02)
        cliente.desactivar();
        assertFalse(cliente.estaActiva(), "Luego de desactivar, la cuenta no debe estar activa");
        assertEquals(EstadoCuenta.INACTIVA, cliente.getEstadoCuenta());

        // Activar nuevamente (HU 02)
        cliente.activar();
        assertTrue(cliente.estaActiva(), "Luego de activar, la cuenta debe estar activa nuevamente");
        assertEquals(EstadoCuenta.ACTIVA, cliente.getEstadoCuenta());
    }

    @Test
    @DisplayName("La lista de facturas de un cliente debe inicializarse vacía")
    void testListaFacturasInicialmenteVacia() {
        Cliente c = new Cliente();
        assertNotNull(c.getFacturas(), "La lista de facturas no debe ser nula");
        assertEquals(0, c.getFacturas().size(), "Inicialmente no debe haber facturas asociadas");
    }

    @Test
    @DisplayName("Debe poder asociar facturas al cliente y mantener la colección")
    void testAsociarFacturasACliente() {
        Factura f1 = new Factura();
        Factura f2 = new Factura();

        cliente.setFacturas(new ArrayList<>());
        cliente.getFacturas().add(f1);
        cliente.getFacturas().add(f2);

        assertEquals(2, cliente.getFacturas().size(), "El cliente debería tener 2 facturas asociadas");
        assertTrue(cliente.getFacturas().contains(f1));
        assertTrue(cliente.getFacturas().contains(f2));
    }
}
