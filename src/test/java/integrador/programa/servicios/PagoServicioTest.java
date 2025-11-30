package integrador.programa.servicios;

import integrador.programa.modelo.*;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.repositorios.FacturaRepositorio;
import integrador.programa.repositorios.PagoRepositorio;
import integrador.programa.repositorios.ReciboRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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

    private Cliente crearCliente(Long id, String nombre) {
        Cliente c = new Cliente();
        ReflectionTestUtils.setField(c, "idCuenta", id);
        c.setNombre(nombre);
        return c;
    }

    private Factura crearFactura(Cliente cliente, double precio, LocalDate fecha) {
        Factura f = new Factura();
        ReflectionTestUtils.setField(f, "idFactura", new Random().nextLong(1_000_000));
        f.setCliente(cliente);
        f.setPrecioTotal(precio);
        f.setEstado(EstadoFactura.VIGENTE);
        f.setFecha(fecha);
        return f;
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe lanzar excepción si el importe es nulo o menor o igual a cero")
    void registrarPagoYEmitirRecibo_importeInvalido() {
        Long idFactura = 1L;
        Cliente cliente = crearCliente(1L, "Juan");
        Factura factura = crearFactura(cliente, 1000.0, LocalDate.now());

        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.of(factura));

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(idFactura, null,
                        MetodoPago.EFECTIVO, "Empleado", "Obs"));
        assertTrue(ex1.getMessage().contains("mayor a 0"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(idFactura, 0.0,
                        MetodoPago.EFECTIVO, "Empleado", "Obs"));
        assertTrue(ex2.getMessage().contains("mayor a 0"));

        verify(facturaRepositorio, times(2)).findById(idFactura);
        verifyNoMoreInteractions(reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe lanzar excepción si el importe excede el saldo pendiente")
    void registrarPagoYEmitirRecibo_importeExcedeSaldo() {
        Long idFactura = 1L;
        Cliente cliente = crearCliente(1L, "Juan");
        Factura factura = crearFactura(cliente, 1000.0, LocalDate.now()); // saldoPendiente ≈ 1000

        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.of(factura));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(idFactura, 1500.0,
                        MetodoPago.EFECTIVO, "Empleado", "Obs"));

        assertTrue(ex.getMessage().contains("excede el saldo pendiente"));
        verify(facturaRepositorio).findById(idFactura);
        verifyNoMoreInteractions(reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe lanzar excepción si la factura no existe")
    void registrarPagoYEmitirRecibo_facturaNoExiste() {
        Long idFactura = 99L;
        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoYEmitirRecibo(idFactura, 100.0,
                        MetodoPago.EFECTIVO, "Empleado", "Obs"));

        assertTrue(ex.getMessage().contains("Factura no encontrada"));
        verify(facturaRepositorio).findById(idFactura);
        verifyNoMoreInteractions(reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoYEmitirRecibo debe registrar pago completo y marcar la factura como PAGADA")
    void registrarPagoYEmitirRecibo_exitoPagoCompleto() {
        Long idFactura = 1L;
        Cliente cliente = crearCliente(1L, "Juan");
        Factura factura = crearFactura(cliente, 1000.0, LocalDate.now()); // saldoPendiente = 1000

        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.of(factura));
        when(reciboServicio.generarNroRecibo()).thenReturn(123L);
        when(reciboRepositorio.save(any(Recibo.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(pagoRepositorio.save(any(Pago.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        Recibo recibo = pagoServicio.registrarPagoYEmitirRecibo(
                idFactura,
                1000.0,
                MetodoPago.TRANSFERENCIA,
                "Empleado X",
                "Pago total"
        );

        assertNotNull(recibo);
        assertEquals(cliente, recibo.getCliente());
        assertEquals(1000.0, recibo.getImporteTotal());
        assertNotNull(recibo.getPago());
        assertEquals(1000.0, recibo.getPago().getImporte());
        assertEquals(EstadoFactura.PAGADA, factura.getEstado());

        assertEquals(1, recibo.getDetalles().size());
        DetalleRecibo det = recibo.getDetalles().get(0);
        assertEquals(factura, det.getFactura());
        assertEquals(1000.0, det.getImporteAplicado());

        verify(facturaRepositorio, times(2)).save(factura); // una por el estado
        verify(reciboRepositorio, atLeastOnce()).save(any(Recibo.class));
        verify(pagoRepositorio).save(any(Pago.class));
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si el importe es nulo o menor o igual a cero")
    void registrarPagoMasivo_importeInvalido() {
        List<Long> ids = List.of(1L, 2L);

        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(ids, null,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));
        assertTrue(ex1.getMessage().contains("mayor a 0"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(ids, 0.0,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));
        assertTrue(ex2.getMessage().contains("mayor a 0"));

        verifyNoInteractions(facturaRepositorio, reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si no se envían facturas")
    void registrarPagoMasivo_sinFacturas() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(null, 100.0,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));
        assertTrue(ex1.getMessage().contains("Debe seleccionar"));

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(Collections.emptyList(), 100.0,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));
        assertTrue(ex2.getMessage().contains("Debe seleccionar"));

        verifyNoInteractions(facturaRepositorio, reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si no se encuentran todas las facturas")
    void registrarPagoMasivo_noSeEncuentranTodasLasFacturas() {
        List<Long> ids = List.of(1L, 2L);
        Cliente cliente = crearCliente(1L, "Juan");
        Factura f1 = crearFactura(cliente, 500.0, LocalDate.now().minusDays(2));

        when(facturaRepositorio.findAllById(ids))
                .thenReturn(new ArrayList<>(List.of(f1))); // solo una

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(ids, 300.0,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));

        assertTrue(ex.getMessage().contains("No se encontraron todas las facturas"));
        verify(facturaRepositorio).findAllById(ids);
        verifyNoMoreInteractions(reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si las facturas son de distintos clientes")
    void registrarPagoMasivo_facturasDistintoCliente() {
        List<Long> ids = List.of(1L, 2L);

        Cliente c1 = crearCliente(1L, "Juan");
        Cliente c2 = crearCliente(2L, "Ana");

        Factura f1 = crearFactura(c1, 500.0, LocalDate.now().minusDays(2));
        Factura f2 = crearFactura(c2, 500.0, LocalDate.now().minusDays(1));

        when(facturaRepositorio.findAllById(ids))
                .thenReturn(new ArrayList<>(Arrays.asList(f1, f2)));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(ids, 300.0,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));

        assertTrue(ex.getMessage().contains("mismo cliente"));
        verify(facturaRepositorio).findAllById(ids);
        verifyNoMoreInteractions(reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe lanzar excepción si el importe excede la deuda total")
    void registrarPagoMasivo_importeExcedeDeuda() {
        List<Long> ids = List.of(1L, 2L);
        Cliente cliente = crearCliente(1L, "Juan");

        Factura f1 = crearFactura(cliente, 200.0, LocalDate.now().minusDays(3));
        Factura f2 = crearFactura(cliente, 300.0, LocalDate.now().minusDays(2));

        when(facturaRepositorio.findAllById(ids))
                .thenReturn(new ArrayList<>(Arrays.asList(f1, f2))); // LISTA MUTABLE

        // deudaTotal ≈ 500 → importe 600 excede
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.registrarPagoMasivo(ids, 600.0,
                        MetodoPago.EFECTIVO, "Emp", "Obs"));

        assertTrue(ex.getMessage().contains("excede la deuda total"));
        verify(facturaRepositorio).findAllById(ids);
        verifyNoMoreInteractions(reciboRepositorio, pagoRepositorio);
    }

    @Test
    @DisplayName("registrarPagoMasivo debe registrar un pago único y un recibo con detalles distribuyendo el importe")
    void registrarPagoMasivo_exito() {
        List<Long> ids = List.of(1L, 2L);
        Cliente cliente = crearCliente(1L, "Juan");

        Factura f1 = crearFactura(cliente, 1000.0, LocalDate.now().minusDays(3));
        Factura f2 = crearFactura(cliente, 500.0, LocalDate.now().minusDays(1));

        when(facturaRepositorio.findAllById(ids))
                .thenReturn(new ArrayList<>(Arrays.asList(f1, f2))); // LISTA MUTABLE

        when(reciboServicio.generarNroRecibo()).thenReturn(10L);
        when(reciboRepositorio.save(any(Recibo.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(pagoRepositorio.save(any(Pago.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<Pago> pagos = pagoServicio.registrarPagoMasivo(
                ids,
                1200.0, // menor a deuda total (1500)
                MetodoPago.TRANSFERENCIA,
                "Empleado",
                "Pago masivo"
        );

        assertEquals(1, pagos.size());
        Pago pago = pagos.get(0);
        assertEquals(1200.0, pago.getImporte());
        assertNotNull(pago.getRecibo());

        Recibo recibo = pago.getRecibo();
        assertEquals(cliente, recibo.getCliente());
        assertEquals(1200.0, recibo.getImporteTotal());

        assertFalse(recibo.getDetalles().isEmpty());
        double totalDetalles = recibo.getDetalles().stream()
                .mapToDouble(DetalleRecibo::getImporteAplicado)
                .sum();
        assertEquals(1200.0, totalDetalles, 0.001);

        verify(facturaRepositorio, atLeast(1)).save(f1);
        verify(facturaRepositorio, atLeast(1)).save(f2);
        verify(reciboRepositorio, atLeastOnce()).save(any(Recibo.class));
        verify(pagoRepositorio).save(any(Pago.class));
    }

    @Test
    @DisplayName("listarPagosPorFactura debe lanzar excepción si la factura no existe")
    void listarPagosPorFactura_facturaNoExiste() {
        Long idFactura = 99L;
        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.listarPagosPorFactura(idFactura));

        assertTrue(ex.getMessage().contains("Factura no encontrada"));
        verify(facturaRepositorio).findById(idFactura);
    }

    @Test
    @DisplayName("listarPagosPorFactura debe devolver los pagos asociados a los DetalleRecibo de la factura (sin duplicados)")
    void listarPagosPorFactura_ok() {
        Long idFactura = 1L;
        Cliente cliente = crearCliente(1L, "Juan");
        Factura factura = crearFactura(cliente, 1000.0, LocalDate.now());

        Recibo r1 = new Recibo();
        Recibo r2 = new Recibo();

        Pago p1 = Pago.builder().idPago(1L).importe(100.0).recibo(r1).build();
        Pago p2 = Pago.builder().idPago(2L).importe(200.0).recibo(r2).build();

        r1.setPago(p1);
        r2.setPago(p2);

        DetalleRecibo d1 = new DetalleRecibo();
        d1.setRecibo(r1);
        d1.setFactura(factura);

        DetalleRecibo d2 = new DetalleRecibo();
        d2.setRecibo(r2);
        d2.setFactura(factura);

        DetalleRecibo d3 = new DetalleRecibo();
        d3.setRecibo(r1); // mismo recibo → mismo pago (para probar sin duplicados)
        d3.setFactura(factura);

        factura.getDetallesRecibo().addAll(Arrays.asList(d1, d2, d3));

        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.of(factura));

        List<Pago> pagos = pagoServicio.listarPagosPorFactura(idFactura);

        assertEquals(2, pagos.size());
        assertTrue(pagos.contains(p1));
        assertTrue(pagos.contains(p2));
        verify(facturaRepositorio).findById(idFactura);
    }

    @Test
    @DisplayName("listarPagosPorCliente debe devolver los pagos de los recibos del cliente indicado")
    void listarPagosPorCliente_ok() {
        Long idCliente = 1L;
        Cliente cliente1 = crearCliente(1L, "Juan");
        Cliente cliente2 = crearCliente(2L, "Ana");

        Recibo r1 = new Recibo();
        r1.setCliente(cliente1);
        Recibo r2 = new Recibo();
        r2.setCliente(cliente1);
        Recibo r3 = new Recibo();
        r3.setCliente(cliente2);

        Pago p1 = Pago.builder().idPago(1L).importe(100.0).build();
        Pago p2 = Pago.builder().idPago(2L).importe(200.0).build();
        Pago p3 = Pago.builder().idPago(3L).importe(300.0).build();

        r1.setPago(p1);
        r2.setPago(p2);
        r3.setPago(p3);

        when(reciboRepositorio.findAll()).thenReturn(Arrays.asList(r1, r2, r3));

        List<Pago> pagos = pagoServicio.listarPagosPorCliente(idCliente);

        assertEquals(2, pagos.size());
        assertTrue(pagos.contains(p1));
        assertTrue(pagos.contains(p2));
        assertFalse(pagos.contains(p3));
        verify(reciboRepositorio).findAll();
    }

    @Test
    @DisplayName("calcularTotalPagadoPorFactura debe lanzar excepción si la factura no existe")
    void calcularTotalPagadoPorFactura_facturaNoExiste() {
        Long idFactura = 99L;
        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> pagoServicio.calcularTotalPagadoPorFactura(idFactura));

        assertTrue(ex.getMessage().contains("Factura no encontrada"));
        verify(facturaRepositorio).findById(idFactura);
    }

    @Test
    @DisplayName("calcularTotalPagadoPorFactura debe devolver el total pagado según la entidad Factura")
    void calcularTotalPagadoPorFactura_ok() {
        Long idFactura = 1L;

        // Subclase sencilla para controlar el valor de calcularTotalPagado()
        Factura factura = new Factura() {
            @Override
            public double calcularTotalPagado() {
                return 250.0;
            }
        };

        when(facturaRepositorio.findById(idFactura)).thenReturn(Optional.of(factura));

        Double total = pagoServicio.calcularTotalPagadoPorFactura(idFactura);

        assertEquals(250.0, total);
        verify(facturaRepositorio).findById(idFactura);
    }

    @Test
    @DisplayName("buscarPorId debe delegar en el repositorio")
    void buscarPorId_ok() {
        Long idPago = 5L;
        Pago pago = Pago.builder().idPago(idPago).importe(123.0).build();

        when(pagoRepositorio.findById(idPago)).thenReturn(Optional.of(pago));

        Optional<Pago> resultado = pagoServicio.buscarPorId(idPago);

        assertTrue(resultado.isPresent());
        assertEquals(pago, resultado.get());
        verify(pagoRepositorio).findById(idPago);
    }
}
