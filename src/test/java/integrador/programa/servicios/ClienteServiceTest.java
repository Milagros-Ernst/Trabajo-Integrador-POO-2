package integrador.programa.servicios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.repositorios.ClienteRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepositorio clienteRepositorio;

    @InjectMocks
    private ClienteService clienteService;

    private Cliente clienteBase;

    @BeforeEach
    void setUp() {
        clienteBase = new Cliente();
        ReflectionTestUtils.setField(clienteBase, "idCuenta", 1L);
        clienteBase.setNombre("Juan");
        clienteBase.setApellido("Pérez");
        // suponemos que estos setters existen en Cliente
        clienteBase.setNumeroDocumento("12345678");
        clienteBase.setTipoDocumento(null); // evitamos depender de un valor específico del enum
    }

    @Test
    @DisplayName("crearCliente debe guardar el cliente cuando el documento no existe")
    void crearCliente_exito() {
        when(clienteRepositorio.existsByTipoDocumentoAndNumeroDocumento(
                clienteBase.getTipoDocumento(),
                clienteBase.getNumeroDocumento()
        )).thenReturn(false);

        when(clienteRepositorio.save(clienteBase)).thenReturn(clienteBase);

        Cliente resultado = clienteService.crearCliente(clienteBase);

        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());
        verify(clienteRepositorio).existsByTipoDocumentoAndNumeroDocumento(null, "12345678");
        verify(clienteRepositorio).save(clienteBase);
    }

    @Test
    @DisplayName("crearCliente debe lanzar excepción cuando el documento ya existe")
    void crearCliente_documentoDuplicado() {
        when(clienteRepositorio.existsByTipoDocumentoAndNumeroDocumento(
                clienteBase.getTipoDocumento(),
                clienteBase.getNumeroDocumento()
        )).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.crearCliente(clienteBase)
        );

        assertTrue(ex.getMessage().contains("Ya existe un cliente con ese documento"));
        verify(clienteRepositorio).existsByTipoDocumentoAndNumeroDocumento(null, "12345678");
        verify(clienteRepositorio, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("buscarPorId debe devolver el cliente cuando existe")
    void buscarPorId_existe() {
        when(clienteRepositorio.findById(1L)).thenReturn(Optional.of(clienteBase));

        Cliente resultado = clienteService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdCuenta());
        verify(clienteRepositorio).findById(1L);
    }

    @Test
    @DisplayName("buscarPorId debe lanzar excepción cuando el cliente no existe")
    void buscarPorId_noExiste() {
        when(clienteRepositorio.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.buscarPorId(99L)
        );

        assertTrue(ex.getMessage().contains("No se encontró el cliente con ID: 99"));
        verify(clienteRepositorio).findById(99L);
    }

    @Test
    @DisplayName("buscarPorDocumento debe delegar correctamente en el repositorio")
    void buscarPorDocumento_ok() {
        when(clienteRepositorio.findByTipoDocumentoAndNumeroDocumento(null, "12345678"))
                .thenReturn(Optional.of(clienteBase));

        var resultado = clienteService.buscarPorDocumento(null, "12345678");

        assertTrue(resultado.isPresent());
        assertEquals(clienteBase, resultado.get());
        verify(clienteRepositorio)
                .findByTipoDocumentoAndNumeroDocumento(null, "12345678");
    }

    @Test
    @DisplayName("modificarCliente debe actualizar datos cuando el documento nuevo no está en uso")
    void modificarCliente_exitoDocumentoLibre() {
        Long id = 1L;

        Cliente clienteExistente = new Cliente();
        ReflectionTestUtils.setField(clienteExistente, "idCuenta", id);
        clienteExistente.setNumeroDocumento("11111111");
        clienteExistente.setTipoDocumento(null);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setNombre("Nombre nuevo");
        clienteActualizado.setApellido("Apellido nuevo");
        clienteActualizado.setNumeroDocumento("22222222");
        clienteActualizado.setTipoDocumento(null);

        when(clienteRepositorio.findById(id)).thenReturn(Optional.of(clienteExistente));
        // no hay otro cliente usando ese documento
        when(clienteRepositorio.findByTipoDocumentoAndNumeroDocumento(null, "22222222"))
                .thenReturn(Optional.empty());
        when(clienteRepositorio.save(clienteActualizado)).thenReturn(clienteActualizado);

        Cliente resultado = clienteService.modificarCliente(id, clienteActualizado);

        assertNotNull(resultado);
        assertEquals(id, resultado.getIdCuenta());
        assertEquals("Nombre nuevo", resultado.getNombre());
        assertEquals("22222222", resultado.getNumeroDocumento());

        verify(clienteRepositorio).findById(id);
        verify(clienteRepositorio)
                .findByTipoDocumentoAndNumeroDocumento(null, "22222222");
        verify(clienteRepositorio).save(clienteActualizado);
    }

    @Test
    @DisplayName("modificarCliente debe lanzar excepción cuando el documento está en uso por otro cliente")
    void modificarCliente_documentoEnUsoPorOtroCliente() {
        Long id = 1L;

        Cliente clienteExistente = new Cliente();
        ReflectionTestUtils.setField(clienteExistente, "idCuenta", id);
        clienteExistente.setNumeroDocumento("11111111");
        clienteExistente.setTipoDocumento(null);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setNumeroDocumento("22222222");
        clienteActualizado.setTipoDocumento(null);

        Cliente otroCliente = new Cliente();
        ReflectionTestUtils.setField(otroCliente, "idCuenta", 2L); // distinto ID

        when(clienteRepositorio.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepositorio.findByTipoDocumentoAndNumeroDocumento(null, "22222222"))
                .thenReturn(Optional.of(otroCliente));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.modificarCliente(id, clienteActualizado)
        );

        assertTrue(ex.getMessage().contains("El documento ya está en uso por otro cliente"));

        verify(clienteRepositorio).findById(id);
        verify(clienteRepositorio)
                .findByTipoDocumentoAndNumeroDocumento(null, "22222222");
        verify(clienteRepositorio, never()).save(any(Cliente.class));
    }

    @Test
    @DisplayName("modificarCliente no debe validar documento cuando no cambia número ni tipo")
    void modificarCliente_mismoDocumentoNoValidaOtraVez() {
        Long id = 1L;

        Cliente clienteExistente = new Cliente();
        ReflectionTestUtils.setField(clienteExistente, "idCuenta", id);
        clienteExistente.setNumeroDocumento("11111111");
        clienteExistente.setTipoDocumento(null);

        Cliente clienteActualizado = new Cliente();
        clienteActualizado.setNumeroDocumento("11111111");
        clienteActualizado.setTipoDocumento(null);
        clienteActualizado.setNombre("Nuevo");

        when(clienteRepositorio.findById(id)).thenReturn(Optional.of(clienteExistente));
        when(clienteRepositorio.save(clienteActualizado)).thenReturn(clienteActualizado);

        Cliente resultado = clienteService.modificarCliente(id, clienteActualizado);

        assertEquals("Nuevo", resultado.getNombre());

        verify(clienteRepositorio).findById(id);
        // no se debería consultar por documento porque no cambió
        verify(clienteRepositorio, never())
                .findByTipoDocumentoAndNumeroDocumento(any(), anyString());
        verify(clienteRepositorio).save(clienteActualizado);
    }

    @Test
    @DisplayName("bajaCliente debe desactivar el cliente y guardarlo")
    void bajaCliente_ok() {
        Long id = 1L;

        Cliente clienteSpy = Mockito.spy(new Cliente());
        ReflectionTestUtils.setField(clienteSpy, "idCuenta", id);

        when(clienteRepositorio.findById(id)).thenReturn(Optional.of(clienteSpy));
        when(clienteRepositorio.save(clienteSpy)).thenReturn(clienteSpy);

        Cliente resultado = clienteService.bajaCliente(id);

        assertNotNull(resultado);
        verify(clienteRepositorio).findById(id);
        verify(clienteSpy).desactivar();
        verify(clienteRepositorio).save(clienteSpy);
    }

    @Test
    @DisplayName("reactivarCliente debe activar el cliente y guardarlo")
    void reactivarCliente_ok() {
        Long id = 1L;

        Cliente clienteSpy = Mockito.spy(new Cliente());
        ReflectionTestUtils.setField(clienteSpy, "idCuenta", id);

        when(clienteRepositorio.findById(id)).thenReturn(Optional.of(clienteSpy));
        when(clienteRepositorio.save(clienteSpy)).thenReturn(clienteSpy);

        Cliente resultado = clienteService.reactivarCliente(id);

        assertNotNull(resultado);
        verify(clienteRepositorio).findById(id);
        verify(clienteSpy).activar();
        verify(clienteRepositorio).save(clienteSpy);
    }

    @Test
    @DisplayName("listarClientesActivos debe devolver solo clientes en estado ACTIVA")
    void listarClientesActivos_ok() {
        Cliente c1 = new Cliente();
        Cliente c2 = new Cliente();
        when(clienteRepositorio.findByEstadoCuenta(EstadoCuenta.ACTIVA))
                .thenReturn(List.of(c1, c2));

        List<Cliente> resultado = clienteService.listarClientesActivos();

        assertEquals(2, resultado.size());
        verify(clienteRepositorio).findByEstadoCuenta(EstadoCuenta.ACTIVA);
    }

    @Test
    @DisplayName("listarClientesInactivos debe devolver solo clientes en estado INACTIVA")
    void listarClientesInactivos_ok() {
        Cliente c1 = new Cliente();
        when(clienteRepositorio.findByEstadoCuenta(EstadoCuenta.INACTIVA))
                .thenReturn(List.of(c1));

        List<Cliente> resultado = clienteService.listarClientesInactivos();

        assertEquals(1, resultado.size());
        verify(clienteRepositorio).findByEstadoCuenta(EstadoCuenta.INACTIVA);
    }
}
