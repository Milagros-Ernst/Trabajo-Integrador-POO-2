package integrador.programa.servicios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Recibo;
import integrador.programa.modelo.DetalleRecibo;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.repositorios.PagoRepositorio;
import integrador.programa.repositorios.FacturaRepositorio;
import integrador.programa.repositorios.ReciboRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
@Transactional
public class PagoServicio {

    private final PagoRepositorio pagoRepositorio;
    private final FacturaRepositorio facturaRepositorio;
    private final ReciboRepositorio reciboRepositorio;

    public PagoServicio(PagoRepositorio pagoRepositorio,
                        FacturaRepositorio facturaRepositorio,
                        ReciboRepositorio reciboRepositorio) {
        this.pagoRepositorio = pagoRepositorio;
        this.facturaRepositorio = facturaRepositorio;
        this.reciboRepositorio = reciboRepositorio;
    }

    // PAGO MASIVO: UN PAGO / UN RECIBO / VARIAS FACTURAS
    @Transactional
    public Pago registrarPagoMasivo(List<Long> idsFacturas,
                                    Double importeTotal,
                                    MetodoPago metodoPago,
                                    String empleadoResponsable,
                                    String observaciones) {

        if (importeTotal == null || importeTotal <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor a 0.");
        }

        // Obtenemos las facturas desde el repositorio
        List<Factura> facturas = facturaRepositorio.findAllById(idsFacturas);
        if (facturas.size() != idsFacturas.size()) {
            throw new IllegalArgumentException("No se encontraron todas las facturas solicitadas.");
        }

        if (facturas.isEmpty()) {
            throw new IllegalArgumentException("No se seleccionó ninguna factura.");
        }

        // Validar que todas las facturas correspondan al mismo cliente
        Cliente cliente = facturas.get(0).getCliente();
        boolean mismoCliente = facturas.stream()
                .allMatch(f -> f.getCliente().getIdCuenta().equals(cliente.getIdCuenta()));

        if (!mismoCliente) {
            throw new IllegalArgumentException("Todas las facturas de un mismo pago deben ser del mismo cliente.");
        }

        // Ordenamos para pagar primero las facturas más viejas
        facturas.sort(Comparator.comparing(Factura::getFecha));

        // Calculamos la deuda total (usando DetalleRecibo vía calcularSaldoPendiente)
        double deudaTotal = facturas.stream()
                .mapToDouble(Factura::calcularSaldoPendiente)
                .sum();

        double EPSILON = 0.001;

        if (importeTotal > deudaTotal + EPSILON) {
            throw new IllegalArgumentException(
                    String.format("El importe ($%.2f) excede la deuda total seleccionada ($%.2f)",
                            importeTotal, deudaTotal));
        }

        // 1) Crear el RECIBO (vacío)
        Recibo recibo = new Recibo();
        recibo.setCliente(cliente);
        recibo.setImporteTotal(0.0); // se actualiza después

        Recibo reciboPersistido = reciboRepositorio.save(recibo);

        // 2) Crear el PAGO global asociado al RECIBO
        Pago pago = Pago.builder()
                .importe(importeTotal)
                .metodoPago(metodoPago)
                .empleadoResponsable(empleadoResponsable)
                .observaciones(observaciones)
                .recibo(reciboPersistido)
                .build();

        Pago pagoGuardado = pagoRepositorio.save(pago);
        reciboPersistido.setPago(pagoGuardado);

        // 3) Distribuir el importe sobre las facturas mediante DetalleRecibo
        double remanente = importeTotal;
        double totalAplicado = 0.0;

        List<DetalleRecibo> detalles = new ArrayList<>();

        for (Factura factura : facturas) {

            if (remanente < EPSILON) break;

            double saldoActual = factura.calcularSaldoPendiente();

            if (saldoActual < EPSILON) continue;

            double montoAPagar = Math.min(saldoActual, remanente);
            if (montoAPagar < EPSILON) continue;

            DetalleRecibo detalle = new DetalleRecibo();
            detalle.setRecibo(reciboPersistido);
            detalle.setFactura(factura);
            detalle.setImporteAplicado(montoAPagar);
            detalles.add(detalle);

            // Actualizar estado de la factura
            double nuevoSaldo = saldoActual - montoAPagar;
            if (nuevoSaldo < EPSILON) {
                factura.setEstado(EstadoFactura.PAGADA);
            } else {
                factura.setEstado(EstadoFactura.PARCIAL);
            }
            facturaRepositorio.save(factura);

            remanente -= montoAPagar;
            totalAplicado += montoAPagar;
        }

        // Asociar detalles al recibo
        reciboPersistido.setDetalles(detalles);
        reciboPersistido.setImporteTotal(totalAplicado);

        // Guardar recibo con sus detalles
        reciboRepositorio.save(reciboPersistido);

        // Devolvemos el pago global (un solo Pago)
        return pagoGuardado;
    }

    // PAGO INDIVIDUAL + EMISIÓN DE RECIBO (1 factura)
    @Transactional
    public Recibo registrarPagoYEmitirRecibo(
            Long idFactura,
            Double importe,
            MetodoPago metodoPago,
            String empleadoResponsable,
            String observaciones) {

        // 1) Buscar la factura
        Factura factura = facturaRepositorio.findById(idFactura)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));

        // 2) Validar importe contra saldo pendiente actual
        double saldoPendienteAntes = factura.calcularSaldoPendiente();

        if (importe == null || importe <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor a 0.");
        }
        if (importe > saldoPendienteAntes + 0.001) {
            throw new IllegalArgumentException("El importe del pago excede el saldo pendiente de la factura.");
        }

        // 3) Crear el RECIBO vacío (solo con cliente)
        Recibo recibo = new Recibo();
        recibo.setCliente(factura.getCliente());
        recibo.setImporteTotal(0.0); // se actualiza luego

        Recibo reciboPersistido = reciboRepositorio.save(recibo);

        // 4) Crear el PAGO asociado al RECIBO (UNO A UNO)
        Pago pago = Pago.builder()
                .importe(importe)
                .metodoPago(metodoPago)
                .empleadoResponsable(empleadoResponsable)
                .observaciones(observaciones)
                .recibo(reciboPersistido)
                .build();

        Pago pagoGuardado = pagoRepositorio.save(pago);
        reciboPersistido.setPago(pagoGuardado);

        // 5) Actualizar el estado de la factura según el nuevo saldo
        double saldoPendienteDespues = saldoPendienteAntes - importe;

        if (Math.abs(saldoPendienteDespues) < 0.01) {
            factura.setEstado(EstadoFactura.PAGADA);
        } else if (saldoPendienteDespues < factura.getPrecioTotal()) {
            factura.setEstado(EstadoFactura.PARCIAL);
        }

        facturaRepositorio.save(factura);

        // 6) Crear el DETALLE DEL RECIBO (factura única)
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setRecibo(reciboPersistido);
        detalle.setFactura(factura);
        detalle.setImporteAplicado(importe);

        List<DetalleRecibo> detalles = new ArrayList<>();
        detalles.add(detalle);
        reciboPersistido.setDetalles(detalles);

        // 7) Actualizar el importe total del recibo
        reciboPersistido.setImporteTotal(importe);

        // 8) Guardar nuevamente el recibo con su detalle
        Recibo reciboFinal = reciboRepositorio.save(reciboPersistido);

        // 9) Devolver el recibo completo
        return reciboFinal;
    }

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepositorio.findById(id);
    }

    // Pagos (recibos) que han aplicado dinero a una factura.
    public List<Pago> listarPagosPorFactura(Long idFactura) {
        Factura factura = facturaRepositorio.findById(idFactura)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));

        return factura.getDetallesRecibo().stream()
                .map(det -> det.getRecibo().getPago())
                .distinct()
                .collect(Collectors.toList());
    }

    // Pagos totales aplicados a una factura, usando DetalleRecibo.
    public Double calcularTotalPagadoPorFactura(Long idFactura) {
        Factura factura = facturaRepositorio.findById(idFactura)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
        return factura.calcularTotalPagado();
    }

    // Listar pagos por cliente: se hace por todos los recibos del sistema, filtrando por cliente.
    public List<Pago> listarPagosPorCliente(Long idCliente) {
        List<Recibo> todosLosRecibos = reciboRepositorio.findAll();

        return todosLosRecibos.stream()
                .filter(r -> r.getCliente() != null && r.getCliente().getIdCuenta().equals(idCliente))
                .map(Recibo::getPago)
                .filter(p -> p != null)
                .collect(Collectors.toList());
    }
}
