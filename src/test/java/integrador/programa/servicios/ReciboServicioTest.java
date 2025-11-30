package integrador.programa.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Recibo;
import integrador.programa.repositorios.ReciboRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ReciboServicioTest {

    @InjectMocks
    ReciboServicio reciboServicio;

    @Mock
    ReciboRepositorio reciboRepositorio;

    private Recibo recibo;
    private Cliente cliente;
    private List<Recibo> listaRecibos;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        recibo = new Recibo();
        recibo.setIdRecibo(1L);
        recibo.setNroRecibo(10L);
        recibo.setCliente(cliente);
        recibo.setImporteTotal(1500.0);

        listaRecibos = new ArrayList<>();
        listaRecibos.add(recibo);
    }

    @Test
    @DisplayName("Debe generar el número de recibo correlativo en base al último número")
    void testGenerarNroRecibo() {
        when(reciboRepositorio.obtenerUltimoNumero()).thenReturn(10L);

        Long nuevoNumero = reciboServicio.generarNroRecibo();

        assertEquals(11L, nuevoNumero);
        verify(reciboRepositorio, times(1)).obtenerUltimoNumero();
        verifyNoMoreInteractions(reciboRepositorio);
    }

    @Test
    @DisplayName("Debe buscar un recibo existente por ID")
    void testBuscarPorIdExistente() {
        when(reciboRepositorio.findById(1L)).thenReturn(Optional.of(recibo));

        Recibo resultado = reciboServicio.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getIdRecibo());
        assertEquals(10L, resultado.getNroRecibo());
        verify(reciboRepositorio, times(1)).findById(1L);
        verifyNoMoreInteractions(reciboRepositorio);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un recibo inexistente por ID")
    void testBuscarPorIdInexistente() {
        when(reciboRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> reciboServicio.buscarPorId(99L));

        verify(reciboRepositorio, times(1)).findById(99L);
        verifyNoMoreInteractions(reciboRepositorio);
    }

    @Test
    @DisplayName("Debe buscar un recibo existente por número de recibo")
    void testBuscarPorNumeroExistente() {
        when(reciboRepositorio.findByNroRecibo(10L)).thenReturn(recibo);

        Recibo resultado = reciboServicio.buscarPorNumero(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getNroRecibo());
        assertEquals(cliente, resultado.getCliente());
        verify(reciboRepositorio, times(1)).findByNroRecibo(10L);
        verifyNoMoreInteractions(reciboRepositorio);
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar un recibo inexistente por número")
    void testBuscarPorNumeroInexistente() {
        when(reciboRepositorio.findByNroRecibo(999L)).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> reciboServicio.buscarPorNumero(999L));

        verify(reciboRepositorio, times(1)).findByNroRecibo(999L);
        verifyNoMoreInteractions(reciboRepositorio);
    }

    @Test
    @DisplayName("Debe listar los recibos de un cliente")
    void testListarPorCliente() {
        when(reciboRepositorio.findByCliente(cliente)).thenReturn(listaRecibos);

        List<Recibo> resultado = reciboServicio.listarPorCliente(cliente);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(cliente, resultado.get(0).getCliente());
        verify(reciboRepositorio, times(1)).findByCliente(cliente);
        verifyNoMoreInteractions(reciboRepositorio);
    }

    @Test
    @DisplayName("Debe listar todos los recibos")
    void testListarTodos() {
        when(reciboRepositorio.findAll()).thenReturn(listaRecibos);

        List<Recibo> resultado = reciboServicio.listarTodos();

        assertEquals(1, resultado.size());
        assertEquals(recibo, resultado.get(0));
        verify(reciboRepositorio, times(1)).findAll();
        verifyNoMoreInteractions(reciboRepositorio);
    }
}
