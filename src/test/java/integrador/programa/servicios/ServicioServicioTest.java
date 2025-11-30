package integrador.programa.servicios;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
import integrador.programa.repositorios.ServicioRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ServicioServicioTest {

    @Mock
    private ServicioRepositorio servicioRepositorio;

    @InjectMocks
    private ServicioServicio servicioServicio;

    @Test
    @DisplayName("listarTodos debe devolver solo servicios en estado ALTA")
    void testListarTodos() {
        Servicio s1 = new Servicio("Internet 100MB", "Fibra", 5000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        Servicio s2 = new Servicio("Cable HD", "TV HD", 4000.0, TipoIVA.IVA_105, EstadoServicio.ALTA);

        when(servicioRepositorio.findByEstadoServicio(EstadoServicio.ALTA))
                .thenReturn(Arrays.asList(s1, s2));

        List<Servicio> resultado = servicioServicio.listarTodos();

        assertEquals(2, resultado.size());
        assertTrue(resultado.contains(s1));
        assertTrue(resultado.contains(s2));
        verify(servicioRepositorio).findByEstadoServicio(EstadoServicio.ALTA);
    }

    @Test
    @DisplayName("buscarPorId debe devolver el servicio cuando existe")
    void testBuscarPorIdExiste() {
        String id = "abc-123";
        Servicio servicio = new Servicio("Internet", "Desc", 3000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);

        when(servicioRepositorio.findById(id)).thenReturn(Optional.of(servicio));

        Servicio encontrado = servicioServicio.buscarPorId(id);

        assertNotNull(encontrado);
        assertEquals("Internet", encontrado.getNombre());
        verify(servicioRepositorio).findById(id);
    }

    @Test
    @DisplayName("buscarPorId debe lanzar IllegalArgumentException cuando no existe")
    void testBuscarPorIdNoExiste() {
        String id = "no-existe";
        when(servicioRepositorio.findById(id)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> servicioServicio.buscarPorId(id));

        assertTrue(ex.getMessage().contains("No se encontró el servicio con id"));
        verify(servicioRepositorio).findById(id);
    }

    @Test
    @DisplayName("agregarServicio debe forzar estado ALTA si viene null")
    void testAgregarServicioEstadoNuloSePoneAlta() {
        Servicio servicio = new Servicio();
        servicio.setNombre("Internet");
        servicio.setDescripcion("Fibra");
        servicio.setPrecioUnitario(3000.0);
        servicio.setTipoIva(TipoIVA.IVA_21);
        servicio.setEstadoServicio(null); // caso HU 04 con estado sin setear

        when(servicioRepositorio.save(servicio)).thenReturn(servicio);

        Servicio guardado = servicioServicio.agregarServicio(servicio);

        assertEquals(EstadoServicio.ALTA, guardado.getEstadoServicio());
        verify(servicioRepositorio).save(servicio);
    }

    @Test
    @DisplayName("agregarServicio debe respetar el estado si ya viene seteado")
    void testAgregarServicioEstadoDefinidoSeRespeta() {
        Servicio servicio = new Servicio("Internet", "Fibra", 3000.0,
                TipoIVA.IVA_21, EstadoServicio.BAJA); // forzado a BAJA

        when(servicioRepositorio.save(servicio)).thenReturn(servicio);

        Servicio guardado = servicioServicio.agregarServicio(servicio);

        assertEquals(EstadoServicio.BAJA, guardado.getEstadoServicio());
        verify(servicioRepositorio).save(servicio);
    }

    @Test
    @DisplayName("actualizarServicio debe copiar los datos nuevos sobre el existente y guardar")
    void testActualizarServicio() {
        String id = "id-123";

        Servicio existente = new Servicio("Viejo", "Desc vieja", 1000.0,
                TipoIVA.IVA_21, EstadoServicio.ALTA);

        Servicio datosNuevos = new Servicio("Nuevo nombre", "Nueva desc", 2500.0,
                TipoIVA.IVA_105, EstadoServicio.BAJA);

        when(servicioRepositorio.findById(id)).thenReturn(Optional.of(existente));
        when(servicioRepositorio.save(existente)).thenReturn(existente);

        Servicio actualizado = servicioServicio.actualizarServicio(id, datosNuevos);

        assertEquals("Nuevo nombre", actualizado.getNombre());
        assertEquals("Nueva desc", actualizado.getDescripcion());
        assertEquals(2500.0, actualizado.getPrecioUnitario());
        assertEquals(TipoIVA.IVA_105, actualizado.getTipoIva());
        assertEquals(EstadoServicio.BAJA, actualizado.getEstadoServicio());

        verify(servicioRepositorio).findById(id);
        verify(servicioRepositorio).save(existente);
    }

    @Test
    @DisplayName("darDeAlta debe poner estado ALTA y guardar el servicio")
    void testDarDeAlta() {
        String id = "id-456";
        Servicio servicio = new Servicio("Internet", "Desc", 3000.0,
                TipoIVA.IVA_21, EstadoServicio.BAJA);

        when(servicioRepositorio.findById(id)).thenReturn(Optional.of(servicio));
        when(servicioRepositorio.save(servicio)).thenReturn(servicio);

        Servicio result = servicioServicio.darDeAlta(id);

        assertEquals(EstadoServicio.ALTA, result.getEstadoServicio());
        verify(servicioRepositorio).findById(id);
        verify(servicioRepositorio).save(servicio);
    }

    @Test
    @DisplayName("eliminarServicio debe hacer una baja lógica cambiando el estado a BAJA")
    void testEliminarServicioBajaLogica() {
        String id = "id-789";
        Servicio servicio = new Servicio("Internet", "Desc", 3000.0,
                TipoIVA.IVA_21, EstadoServicio.ALTA);

        when(servicioRepositorio.findById(id)).thenReturn(Optional.of(servicio));
        when(servicioRepositorio.save(servicio)).thenReturn(servicio);

        servicioServicio.eliminarServicio(id);

        assertEquals(EstadoServicio.BAJA, servicio.getEstadoServicio());
        verify(servicioRepositorio).findById(id);
        verify(servicioRepositorio).save(servicio);
    }
}
