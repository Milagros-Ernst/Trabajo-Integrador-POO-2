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
    @DisplayName("registrarPagoMasivo debe lanzar excepción si no se envían facturas")
    void registrarPagoMasivo_sinFacturas() {
        IllegalArgumentException ex1 = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        null,
                        100.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );
        assertTrue(ex1.getMessage().contains("Debe seleccionar al menos una factura."));

        IllegalArgumentException ex2 = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        Collections.emptyList(),
                        100.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );
        assertTrue(ex2.getMessage().contains("Debe seleccionar al menos una factura."));

        verifyNoInteractions(facturaRepositorio, reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si no se encuentran todas las facturas")
    void registrarPagoMasivo_noSeEncuentranTodasLasFacturas() {
        List<Long> ids = List.of(1L, 2L, 3L);
        Factura f1 = mock(Factura.class);

        when(facturaRepositorio.findAllById(ids)).thenReturn(List.of(f1));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        ids,
                        100.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );

        assertTrue(ex.getMessage().contains("No se encontraron todas las facturas solicitadas."));
        verify(facturaRepositorio).findAllById(ids);
        verifyNoMoreInteractions(facturaRepositorio);
        verifyNoInteractions(reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si las facturas son de distintos clientes")
    void registrarPagoMasivo_facturasDistintoCliente() {
        List<Long> ids = List.of(1L, 2L);

        Cliente cliente2 = new Cliente();
        ReflectionTestUtils.setField(cliente2, "idCuenta", 2L);

        Factura f1 = mock(Factura.class);
        Factura f2 = mock(Factura.class);

        when(f1.getCliente()).thenReturn(cliente);
        when(f2.getCliente()).thenReturn(cliente2);

        when(facturaRepositorio.findAllById(ids)).thenReturn(List.of(f1, f2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        ids,
                        100.0,
                        null,
                        "Empleado",
                        "Obs"
                )
        );

        assertTrue(ex.getMessage().contains("Todas las facturas de un pago masivo deben ser del mismo cliente."));
        verify(facturaRepositorio).findAllById(ids);
        verifyNoInteractions(reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si el importe excede la deuda total")
    void registrarPagoMasivo_importeExcedeDeuda() {
        List<Long> ids = List.of(1L, 2L);

        Factura f1 = mock(Factura.class);
        Factura f2 = mock(Factura.class);

        when(f1.getCliente()).thenReturn(cliente);
        when(f2.getCliente()).thenReturn(cliente);

        when(f1.getFecha()).thenReturn(LocalDate.of(2024, 1, 1));
        when(f2.getFecha()).thenReturn(LocalDate.of(2024, 2, 1));

        when(f1.calcularSaldoPendiente()).thenReturn(50.0);
        when(f2.calcularSaldoPendiente()).thenReturn(50.0);

        when(facturaRepositorio.findAllById(ids)).thenReturn(List.of(f1, f2));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(
                        ids,
                        200.0,      // mayor a 100 de deuda
                        null,
                        "Empleado",
                        "Obs"
                )
        );

        assertTrue(ex.getMessage().contains("El importe ($200.00) excede la deuda total seleccionada ($100.00)"));
        verify(facturaRepositorio).findAllById(ids);
        verifyNoInteractions(reciboRepositorio, pagoRepositorio, reciboServicio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe registrar un pago único y un recibo con detalles distribuyendo el importe")
    void registrarPagoMasivo_exito() {
        List<Long> ids = List.of(1L, 2L);

        Factura f1 = mock(Factura.class);
        Factura f2 = mock(Factura.class);

        when(f1.getCliente()).thenReturn(cliente);
        when(f2.getCliente()).thenReturn(cliente);

        // Fechas distintas para que se ordenen correctamente
        when(f1.getFecha()).thenReturn(LocalDate.of(2024, 1, 1));
        when(f2.getFecha()).thenReturn(LocalDate.of(2024, 2, 1));

        // saldos pendientes
        when(f1.calcularSaldoPendiente()).thenReturn(100.0);
        when(f2.calcularSaldoPendiente()).thenReturn(50.0);

        when(facturaRepositorio.findAllById(ids)).thenReturn(List.of(f1, f2));

        // generar número de recibo
        when(reciboServicio.generarNroRecibo()).thenReturn(10L);

        // el save de recibo devuelve el mismo objeto que recibe
        when(reciboRepositorio.save(any(Recibo.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // el save de pago devuelve el mismo pago
        when(pagoRepositorio.save(any(Pago.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        double importeTotal = 120.0;

        List<Pago> resultado = pagoServicio.registrarPagoMasivo(
                ids,
                importeTotal,
                null,              // no necesitamos un valor concreto de MetodoPago
                "Empleado X",
                "Obs Y"
        );

        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        Pago pagoGuardado = resultado.get(0);
        assertEquals(importeTotal, pagoGuardado.getImporte());
        assertEquals("Empleado X", pagoGuardado.getEmpleadoResponsable());
        assertEquals("Obs Y", pagoGuardado.getObservaciones());

        verify(facturaRepositorio).findAllById(ids);
        verify(reciboServicio).generarNroRecibo();
        verify(pagoRepositorio).save(any(Pago.class));

        // se deben guardar las facturas con su nuevo estado (PAGADA/PARCIAL)
        verify(facturaRepositorio, times(2)).save(any(Factura.class));
        verify(f1).setEstado(any(EstadoFactura.class));
        verify(f2).setEstado(any(EstadoFactura.class));

        // se debe haber guardado el recibo al menos dos veces (inicial y final)
        verify(reciboRepositorio, atLeast(2)).save(any(Recibo.class));
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
    @DisplayName("buscarPorId debe delegar en el repositorio")
    void buscarPorId_ok() {
        Pago pago = Pago.builder().importe(100.0).build();
        when(pagoRepositorio.findById(1L)).thenReturn(Optional.of(pago));

        Optional<Pago> resultado = pagoServicio.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(100.0, resultado.get().getImporte());
        verify(pagoRepositorio).findById(1L);
    }

    @Test
    @DisplayName("listarPagosPorFactura debe devolver los pagos asociados a los DetalleRecibo de la factura (sin duplicados)")
    void listarPagosPorFactura_ok() {
        Factura factura = mock(Factura.class);

        Pago pago1 = Pago.builder().importe(50.0).build();
        Pago pago2 = Pago.builder().importe(30.0).build();

        Recibo recibo1 = new Recibo();
        recibo1.setPago(pago1);

        Recibo recibo2 = new Recibo();
        recibo2.setPago(pago1); // mismo pago para probar distinct

        Recibo recibo3 = new Recibo();
        recibo3.setPago(pago2);

        DetalleRecibo d1 = new DetalleRecibo();
        d1.setRecibo(recibo1);

        DetalleRecibo d2 = new DetalleRecibo();
        d2.setRecibo(recibo2);

        DetalleRecibo d3 = new DetalleRecibo();
        d3.setRecibo(recibo3);

        when(facturaRepositorio.findById(1L)).thenReturn(Optional.of(factura));
        when(factura.getDetallesRecibo()).thenReturn(List.of(d1, d2, d3));

        List<Pago> pagos = pagoServicio.listarPagosPorFactura(1L);

        assertEquals(2, pagos.size()); // pago1 y pago2
        assertTrue(pagos.contains(pago1));
        assertTrue(pagos.contains(pago2));

        verify(facturaRepositorio).findById(1L);
    }

    @Test
    @DisplayName("listarPagosPorFactura debe lanzar excepción si la factura no existe")
    void listarPagosPorFactura_facturaNoExiste() {
        when(facturaRepositorio.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.listarPagosPorFactura(99L)
        );

        assertTrue(ex.getMessage().contains("Factura no encontrada"));
        verify(facturaRepositorio).findById(99L);
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

    @Test
    @DisplayName("calcularTotalPagadoPorFactura debe lanzar excepción si la factura no existe")
    void calcularTotalPagadoPorFactura_facturaNoExiste() {
        when(facturaRepositorio.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> pagoServicio.calcularTotalPagadoPorFactura(99L)
        );

        assertTrue(ex.getMessage().contains("Factura no encontrada"));
        verify(facturaRepositorio).findById(99L);
    }
}
