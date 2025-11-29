package integrador.programa.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.repositorios.FacturaRepositorio;

/*
 * test basado en el desarrollo del profe, usando MockitoExtension
 * Se simulan los repositorios y servicios externos para aislar la lógica de FacturaServicio.
 */
@ExtendWith(MockitoExtension.class)
public class FacturaServicioTest {

    @InjectMocks
    FacturaServicio facturaServicio;

    @Mock
    FacturaRepositorio facturaRepositorio;

    @Mock
    NotaServicio notaServicio;

    private Factura factura;
    private Cliente cliente;
    private List<Factura> listaFacturas;

    @BeforeEach
    public void iniciar() {
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");

        factura = new Factura();
        ReflectionTestUtils.setField(factura, "idFactura", 1L);
        factura.setCliente(cliente);
        factura.setPrecioTotal(1000.0);
        factura.setEstado(EstadoFactura.VIGENTE); 
        factura.setFecha(LocalDate.now());

        listaFacturas = new ArrayList<>();
        listaFacturas.add(factura);
    }

    @Test
    @DisplayName("Debe listar todas las facturas")
    public void listarFacturas() {
        when(facturaRepositorio.findAll()).thenReturn(listaFacturas);
        var resultado = facturaServicio.listarFacturas();
        assertEquals(1, resultado.size());
        assertEquals(factura, resultado.get(0));
        verify(facturaRepositorio, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe guardar una factura correctamente")
    public void agregarFactura() {
        when(facturaRepositorio.save(factura)).thenReturn(factura);
        Factura resultado = facturaServicio.agregarFactura(factura);
        verify(facturaRepositorio, times(1)).save(factura);
        assertNotNull(resultado);
        assertEquals(1000.0, resultado.getPrecioTotal());
    }

    @Test
    @DisplayName("Debe buscar factura por ID existente")
    public void buscarPorId() {
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        Optional<Factura> resultado = facturaServicio.buscarPorId(1L);
        verify(facturaRepositorio, times(1)).findById(1L);
        assertTrue(resultado.isPresent());
        assertEquals(factura.getIdFactura(), resultado.get().getIdFactura());
    }

    @Test
    @DisplayName("Debe buscar facturas por Cliente")
    public void buscarFacturasPorCliente() {
        when(facturaRepositorio.findByCliente(cliente)).thenReturn(listaFacturas);
        List<Factura> resultado = facturaServicio.buscarFacturasPorCliente(cliente);
        verify(facturaRepositorio, times(1)).findByCliente(cliente);
        assertFalse(resultado.isEmpty());
        assertEquals(cliente, resultado.get(0).getCliente());
    }

    @Test
    @DisplayName("Debe realizar la anulación de una factura VIGENTE")
    public void bajaFacturaExitosa() {
        String motivo = "Error de facturación";
        NotaCredito notaMock = new NotaCredito(); 
        
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        when(notaServicio.altaNotaPorFactura(factura, motivo)).thenReturn(notaMock);
        when(facturaRepositorio.save(factura)).thenReturn(factura);

        NotaCredito resultado = facturaServicio.bajaFactura(1L, motivo);

        // verifica que se haya buscado la factura
        verify(facturaRepositorio, times(1)).findById(1L);
        
        // verifica que e haya llamado a notaServicio para el alta de nota
        verify(notaServicio, times(1)).altaNotaPorFactura(factura, motivo);
        
        // verifica el cambio de estado de factura
        verify(facturaRepositorio, times(1)).save(factura);
        assertEquals(EstadoFactura.ANULADA, factura.getEstado());
        assertNotNull(resultado);
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar anular una factura que no existe")
    public void bajaFacturaIdInexistente() {
        when(facturaRepositorio.findById(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> facturaServicio.bajaFactura(99L, "Motivo X"));

        verify(facturaRepositorio, times(1)).findById(99L);
        verifyNoMoreInteractions(notaServicio);
        verifyNoMoreInteractions(facturaRepositorio); 
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar anular una factura que no está VIGENTE")
    public void bajaFacturaEstadoIncorrecto() {
        factura.setEstado(EstadoFactura.PAGADA);
        
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));

        assertThrows(IllegalStateException.class, () -> facturaServicio.bajaFactura(1L, "Motivo X"));

        verify(facturaRepositorio, times(1)).findById(1L);
        verifyNoMoreInteractions(notaServicio);
    }
}