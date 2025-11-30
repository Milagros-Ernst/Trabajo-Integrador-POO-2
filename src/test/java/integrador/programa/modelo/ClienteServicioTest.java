package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.EstadoServicio;

public class ClienteServicioTest {

    private ClienteServicio clienteServicio;
    private Cliente cliente;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        clienteServicio = new ClienteServicio();

        cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        servicio = new Servicio();
        servicio.setNombre("Internet 100MB");

        clienteServicio.setCliente(cliente);
        clienteServicio.setServicio(servicio);
    }

    @Test
    @DisplayName("Debe vincular correctamente un cliente y un servicio")
    void testAsociacionClienteServicio() {
        assertNotNull(clienteServicio.getCliente(), "El cliente no debería ser nulo");
        assertNotNull(clienteServicio.getServicio(), "El servicio no debería ser nulo");

        assertEquals("Juan", clienteServicio.getCliente().getNombre());
        assertEquals("Internet 100MB", clienteServicio.getServicio().getNombre());
    }

    @Test
    @DisplayName("Dar de alta un servicio para el cliente debe poner estado ALTA y limpiar fecha de baja")
    void testDarDeAltaServicioCliente() {
        LocalDate fechaAlta = LocalDate.of(2024, 5, 10);

        clienteServicio.darDeAlta(fechaAlta);

        assertEquals(EstadoServicio.ALTA, clienteServicio.getEstadoServicio());
        assertEquals(fechaAlta, clienteServicio.getFechaAlta());
        assertNull(clienteServicio.getFechaBaja(), "Al dar de alta, la fecha de baja debe ser null");
        assertTrue(clienteServicio.estaActivo(), "Con estado ALTA y sin fecha de baja debe estar activo");
    }

    @Test
    @DisplayName("Dar de baja un servicio para el cliente debe poner estado BAJA y registrar fecha de baja")
    void testDarDeBajaServicioCliente() {
        LocalDate fechaAlta = LocalDate.of(2024, 5, 1);
        LocalDate fechaBaja = LocalDate.of(2024, 5, 20);

        clienteServicio.darDeAlta(fechaAlta);
        clienteServicio.darDeBaja(fechaBaja);

        assertEquals(EstadoServicio.BAJA, clienteServicio.getEstadoServicio());
        assertEquals(fechaAlta, clienteServicio.getFechaAlta(), "La fecha de alta no debería cambiar");
        assertEquals(fechaBaja, clienteServicio.getFechaBaja(), "La fecha de baja debe registrarse correctamente");
        assertFalse(clienteServicio.estaActivo(), "Con estado BAJA no debería estar activo");
    }

    @Test
    @DisplayName("estaActivo solo debe ser true cuando el estado es ALTA y no hay fecha de baja")
    void testEstaActivoSegunEstadoYFechas() {
        // Caso 1: ALTA y sin fecha de baja -> activo
        clienteServicio.darDeAlta(LocalDate.of(2024, 5, 10));
        assertTrue(clienteServicio.estaActivo());

        // Caso 2: ALTA pero con fecha de baja -> no activo
        clienteServicio.setFechaBaja(LocalDate.of(2024, 5, 20));
        assertFalse(clienteServicio.estaActivo(), "Si tiene fecha de baja no debería estar activo");

        // Caso 3: BAJA y fecha de baja -> no activo
        clienteServicio.setEstadoServicio(EstadoServicio.BAJA);
        assertFalse(clienteServicio.estaActivo());
    }

    @Test
    @DisplayName("toString debe incluir información básica de cliente, servicio y estados")
    void testToStringContieneInformacionRelevante() {
        clienteServicio.darDeAlta(LocalDate.of(2024, 5, 10));
        String texto = clienteServicio.toString();

        assertTrue(texto.contains("Juan"));
        assertTrue(texto.contains("Internet 100MB"));
        assertTrue(texto.contains("ALTA"));
    }
}
