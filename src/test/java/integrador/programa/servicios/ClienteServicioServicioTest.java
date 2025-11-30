package integrador.programa.servicios;

// --- 1. IMPORTS DEL MODELO (DATOS) ---
import integrador.programa.modelo.ClienteServicio; // La entidad de la relación
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;

// --- 2. IMPORTS DE LOS REPOSITORIOS ---
import integrador.programa.repositorios.ClienteRepositorio;
import integrador.programa.repositorios.ClienteServicioRepositorio;
import integrador.programa.repositorios.ServicioRepositorio;

// --- 3. IMPORT DE LA LÓGICA (EL SERVICIO QUE PROBAMOS) ---
// Según tu imagen, la clase se llama así:
import integrador.programa.servicios.ClienteServicioServicio;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServicioServicioTest {

    @Mock
    private ClienteServicioRepositorio clienteServicioRepositorio;

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @Mock
    private ServicioRepositorio servicioRepositorio;

    @InjectMocks
    private ClienteServicioServicio clienteServicioServicio;

    private Cliente cliente;
    private Servicio servicio;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        Long idCliente = 1L; // este valor se usa para buscar, no para setear el ID
        cliente.setNombre("Juan");

        servicio = new Servicio("Internet 100MB", "Fibra", 5000.0,
                TipoIVA.IVA_21, EstadoServicio.ALTA);
    }

    @Test
    @DisplayName("asignarServicioACliente debe crear una nueva relación en ALTA si no existía")
    void testAsignarServicioClienteNuevaRelacion() {
        Long idCliente = 1L;
        String idServicio = "serv-1";

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(servicioRepositorio.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(clienteServicioRepositorio.findByClienteAndServicio(cliente, servicio))
                .thenReturn(Optional.empty());

        when(clienteServicioRepositorio.save(any(ClienteServicio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ClienteServicio cs = clienteServicioServicio.asignarServicioACliente(idCliente, idServicio);

        assertNotNull(cs);
        assertEquals(cliente, cs.getCliente());
        assertEquals(servicio, cs.getServicio());
        assertEquals(EstadoServicio.ALTA, cs.getEstadoServicio());
        assertNotNull(cs.getFechaAlta());
        assertNull(cs.getFechaBaja());

        verify(clienteRepositorio).findById(idCliente);
        verify(servicioRepositorio).findById(idServicio);
        verify(clienteServicioRepositorio).findByClienteAndServicio(cliente, servicio);
        verify(clienteServicioRepositorio).save(any(ClienteServicio.class));
    }

    @Test
    @DisplayName("asignarServicioACliente debe lanzar IllegalStateException si ya estaba activo")
    void testAsignarServicioClienteYaActivo() {
        Long idCliente = 1L;
        String idServicio = "serv-1";

        ClienteServicio existente = new ClienteServicio();
        existente.setCliente(cliente);
        existente.setServicio(servicio);
        existente.darDeAlta(LocalDate.now()); // ALTA y sin fechaBaja → activo

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(servicioRepositorio.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(clienteServicioRepositorio.findByClienteAndServicio(cliente, servicio))
                .thenReturn(Optional.of(existente));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clienteServicioServicio.asignarServicioACliente(idCliente, idServicio));

        assertTrue(ex.getMessage().contains("ya tiene dado de alta este servicio"));

        verify(clienteServicioRepositorio, never()).save(any());
    }

    @Test
    @DisplayName("asignarServicioACliente debe reactivar una relación existente dada de baja")
    void testAsignarServicioClienteReactivar() {
        Long idCliente = 1L;
        String idServicio = "serv-1";

        ClienteServicio existente = new ClienteServicio();
        existente.setCliente(cliente);
        existente.setServicio(servicio);
        existente.darDeAlta(LocalDate.of(2024, 1, 1));
        existente.darDeBaja(LocalDate.of(2024, 2, 1)); // ahora está BAJA

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(servicioRepositorio.findById(idServicio)).thenReturn(Optional.of(servicio));
        when(clienteServicioRepositorio.findByClienteAndServicio(cliente, servicio))
                .thenReturn(Optional.of(existente));
        when(clienteServicioRepositorio.save(existente)).thenReturn(existente);

        ClienteServicio result = clienteServicioServicio.asignarServicioACliente(idCliente, idServicio);

        assertEquals(EstadoServicio.ALTA, result.getEstadoServicio());
        assertNull(result.getFechaBaja());
        assertNotNull(result.getFechaAlta());

        verify(clienteServicioRepositorio).save(existente);
    }

    @Test
    @DisplayName("asignarServicioACliente debe fallar si el cliente no existe")
    void testAsignarServicioClienteNoExisteCliente() {
        Long idCliente = 99L;
        String idServicio = "serv-1";

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clienteServicioServicio.asignarServicioACliente(idCliente, idServicio));

        assertTrue(ex.getMessage().contains("No se encontró el cliente"));
        verify(servicioRepositorio, never()).findById(anyString());
    }

    @Test
    @DisplayName("asignarServicioACliente debe fallar si el servicio no existe")
    void testAsignarServicioClienteNoExisteServicio() {
        Long idCliente = 1L;
        String idServicio = "serv-99";

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(servicioRepositorio.findById(idServicio)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clienteServicioServicio.asignarServicioACliente(idCliente, idServicio));

        assertTrue(ex.getMessage().contains("No se encontró el servicio"));
        verify(clienteServicioRepositorio, never()).findByClienteAndServicio(any(), any());
    }

    @Test
    @DisplayName("darDeBajaServicioCliente debe hacer baja lógica y guardar")
    void testDarDeBajaServicioCliente() {
        String idClienteServicio = "cs-1";

        ClienteServicio cs = new ClienteServicio();
        cs.setCliente(cliente);
        cs.setServicio(servicio);
        cs.darDeAlta(LocalDate.of(2024, 5, 1)); // ALTA, sin fechaBaja

        when(clienteServicioRepositorio.findById(idClienteServicio))
                .thenReturn(Optional.of(cs));
        when(clienteServicioRepositorio.save(cs)).thenReturn(cs);

        ClienteServicio result = clienteServicioServicio.darDeBajaServicioCliente(idClienteServicio);

        assertEquals(EstadoServicio.BAJA, result.getEstadoServicio());
        assertNotNull(result.getFechaBaja());

        verify(clienteServicioRepositorio).findById(idClienteServicio);
        verify(clienteServicioRepositorio).save(cs);
    }

    @Test
    @DisplayName("darDeBajaServicioCliente debe lanzar IllegalStateException si ya estaba de baja")
    void testDarDeBajaServicioClienteYaBaja() {
        String idClienteServicio = "cs-2";

        ClienteServicio cs = new ClienteServicio();
        cs.setCliente(cliente);
        cs.setServicio(servicio);
        cs.darDeAlta(LocalDate.of(2024, 5, 1));
        cs.darDeBaja(LocalDate.of(2024, 5, 20)); // ya está de BAJA

        when(clienteServicioRepositorio.findById(idClienteServicio))
                .thenReturn(Optional.of(cs));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> clienteServicioServicio.darDeBajaServicioCliente(idClienteServicio));

        assertTrue(ex.getMessage().contains("ya estaba dado de baja"));

        verify(clienteServicioRepositorio, never()).save(any());
    }

    @Test
    @DisplayName("darDeBajaServicioCliente debe lanzar IllegalArgumentException si no existe la relación")
    void testDarDeBajaServicioClienteNoExiste() {
        String idClienteServicio = "inexistente";

        when(clienteServicioRepositorio.findById(idClienteServicio))
                .thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> clienteServicioServicio.darDeBajaServicioCliente(idClienteServicio));

        assertTrue(ex.getMessage().contains("No se encontró la relación"));
        verify(clienteServicioRepositorio, never()).save(any());
    }

    @Test
    @DisplayName("listarServiciosDeCliente debe devolver todos los vínculos del cliente")
    void testListarServiciosDeCliente() {
        Long idCliente = 1L;
        ClienteServicio cs = new ClienteServicio();
        cs.setCliente(cliente);
        cs.setServicio(servicio);

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(clienteServicioRepositorio.findByCliente(cliente))
                .thenReturn(Collections.singletonList(cs));

        List<ClienteServicio> lista = clienteServicioServicio.listarServiciosDeCliente(idCliente);

        assertEquals(1, lista.size());
        assertEquals(cliente, lista.get(0).getCliente());
        verify(clienteRepositorio).findById(idCliente);
        verify(clienteServicioRepositorio).findByCliente(cliente);
    }

    @Test
    @DisplayName("listarServiciosActivosDeCliente debe devolver solo vínculos en ALTA")
    void testListarServiciosActivosDeCliente() {
        Long idCliente = 1L;
        ClienteServicio cs = new ClienteServicio();
        cs.setCliente(cliente);
        cs.setServicio(servicio);
        cs.darDeAlta(LocalDate.now());

        when(clienteRepositorio.findById(idCliente)).thenReturn(Optional.of(cliente));
        when(clienteServicioRepositorio.findByClienteAndEstadoServicio(cliente, EstadoServicio.ALTA))
                .thenReturn(Collections.singletonList(cs));

        List<ClienteServicio> lista = clienteServicioServicio.listarServiciosActivosDeCliente(idCliente);

        assertEquals(1, lista.size());
        assertEquals(EstadoServicio.ALTA, lista.get(0).getEstadoServicio());
        verify(clienteRepositorio).findById(idCliente);
        verify(clienteServicioRepositorio)
                .findByClienteAndEstadoServicio(cliente, EstadoServicio.ALTA);
    }
}
