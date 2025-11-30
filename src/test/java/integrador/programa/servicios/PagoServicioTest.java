package integrador.programa.servicios;

import integrador.programa.modelo.*;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.repositorios.FacturaRepositorio;
import integrador.programa.repositorios.PagoRepositorio;
import integrador.programa.repositorios.ReciboRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServicioTest {

    @Mock
    private PagoRepositorio pagoRepositorio;

    @Mock
    private FacturaRepositorio facturaRepositorio;

    @Mock
    private ReciboRepositorio reciboRepositorio;

    @Mock
    private ReciboServicio reciboServicio;

    @InjectMocks
    private PagoServicio pagoServicio;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Cliente Prueba");
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si el importe es nulo o menor o igual a cero")
    void registrarPagoMasivo_importeInvalido() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        List.of(1L, 2L),
                        null,
                        null,
                        "Empleado",
                        "Obs"
                )
        );
        assertTrue(ex1.getMessage().contains("El importe debe ser mayor a 0."));

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        List.of(1L, 2L),
                        0.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );
        assertTrue(ex2.getMessage().contains("El importe debe ser mayor a 0."));

        verifyNoInteractions(facturaRepositorio, reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe lanzar excepción si la factura no existe")
    void registrarPagoYEmitirRecibo_facturaNoExiste() {
        when(facturaRepositorio.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(
                        99L,
                        100.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );

        assertTrue(ex.getMessage().contains("Factura no encontrada"));
        verify(facturaRepositorio).findById(99L);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe lanzar excepción si el importe es nulo o menor o igual a cero")
    void registrarPagoYEmitirRecibo_importeInvalido() {
        Factura factura = mock(Factura.class);
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        when(factura.calcularSaldoPendiente()).thenReturn(100.0);

        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(
                        1L,
                        null,
                        null,
                        "Empleado",
                        "Obs"
                )
        );
        assertTrue(ex1.getMessage().contains("El importe del pago debe ser mayor a 0."));

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(
                        1L,
                        0.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );
        assertTrue(ex2.getMessage().contains("El importe del pago debe ser mayor a 0."));

        verify(facturaRepositorio, times(2)).findById(1L);
        verifyNoInteractions(reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe lanzar excepción si el importe excede el saldo pendiente")
    void registrarPagoYEmitirRecibo_importeExcedeSaldo() {
        Factura factura = mock(Factura.class);
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        when(factura.calcularSaldoPendiente()).thenReturn(100.0);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(
                        1L,
                        150.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );

        assertTrue(ex.getMessage().contains("El importe del pago excede el saldo pendiente de la factura."));
        verify(facturaRepositorio).findById(1L);
        verifyNoInteractions(reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe registrar pago completo y marcar la factura como PAGADA")
    void registrarPagoYEmitirRecibo_exitoPagoCompleto() {
        Factura factura = mock(Factura.class);
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        when(factura.calcularSaldoPendiente()).thenReturn(100.0);
        when(factura.getCliente()).thenReturn(cliente);

        when(reciboServicio.generarNroRecibo()).thenReturn(20L);

        when(reciboRepositorio.save(any(Recibo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(pagoRepositorio.save(any(Pago.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Double importe = 100.0;

        Recibo recibo = pagoServicio.registrarPagoYEmitirRecibo(
                1L,
                importe,
                null,
                "Empleado Y",
                "Obs Z"
        );

        assertNotNull(recibo);
        assertEquals(cliente, recibo.getCliente());
        assertEquals(importe, recibo.getImporteTotal());
        assertEquals(20L, recibo.getNroRecibo());
        assertNotNull(recibo.getPago());
        assertEquals("Empleado Y", recibo.getPago().getEmpleadoResponsable());

        // Se debe actualizar la factura
        verify(facturaRepositorio).save(factura);
        verify(factura).setEstado(EstadoFactura.PAGADA);
        verify(reciboServicio).generarNroRecibo();
        verify(pagoRepositorio).save(any(Pago.class));
        verify(reciboRepositorio, atLeast(2)).save(any(Recibo.class));
    }

    @Test
    @DisplayName("listarPagosPorCliente debe devolver los pagos de los recibos del cliente indicado")
    void listarPagosPorCliente_ok() {
        // cliente con id 1
        Cliente c1 = new Cliente();
        ReflectionTestUtils.setField(c1, "idCuenta", 1L);

        // otro cliente
        Cliente c2 = new Cliente();
        ReflectionTestUtils.setField(c2, "idCuenta", 2L);

        Pago pago1 = Pago.builder().importe(100.0).build();
        Pago pago2 = Pago.builder().importe(50.0).build();
        Pago pagoOtro = Pago.builder().importe(999.0).build();

        Recibo r1 = new Recibo();
        r1.setCliente(c1);
        r1.setPago(pago1);

        Recibo r2 = new Recibo();
        r2.setCliente(c1);
        r2.setPago(pago2);

        Recibo r3 = new Recibo();
        r3.setCliente(c2);
        r3.setPago(pagoOtro);

        Recibo r4 = new Recibo();
        r4.setCliente(c1);
        r4.setPago(null); // sin pago, se debe filtrar

        when(reciboRepositorio.findAll()).thenReturn(List.of(r1, r2, r3, r4));

        List<Pago> pagos = pagoServicio.listarPagosPorCliente(1L);

        assertEquals(2, pagos.size());
        assertTrue(pagos.contains(pago1));
        assertTrue(pagos.contains(pago2));
        assertFalse(pagos.contains(pagoOtro));

        verify(reciboRepositorio).findAll();
    }

    @Test
    @DisplayName("calcularTotalPagadoPorFactura debe devolver el total pagado según la entidad Factura")
    void calcularTotalPagadoPorFactura_ok() {
        Factura factura = mock(Factura.class);
        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        when(factura.calcularTotalPagado()).thenReturn(150.0);

        Double total = pagoServicio.calcularTotalPagadoPorFactura(1L);

        assertEquals(150.0, total);
        verify(facturaRepositorio).findById(1L);
        verify(factura).calcularTotalPagado();
    }

}
