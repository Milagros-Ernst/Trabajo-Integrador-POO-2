package integrador.programa.servicios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Recibo;
import integrador.programa.modelo.DetalleRecibo;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.repositorios.PagoRepositorio;
import integrador.programa.repositorios.FacturaRepositorio;
import integrador.programa.repositorios.ReciboRepositorio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

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

    // PAGO MASIVO
    @Transactional
    public List<Pago> registrarPagoMasivo(List<Long> idsFacturas,
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

        // Ordenamos para pagar primero las facturas más viejas
        facturas.sort(Comparator.comparing(Factura::getFecha));

        // Calculamos la deuda total
        double deudaTotal = facturas.stream()
                .mapToDouble(Factura::calcularSaldoPendiente)
                .sum();

        double EPSILON = 0.001;

        if (importeTotal > deudaTotal + EPSILON) {
            throw new IllegalArgumentException(
                    String.format("El importe ($%.2f) excede la deuda total seleccionada ($%.2f)",
                            importeTotal, deudaTotal));
        }

        List<Pago> pagosAInsertar = new ArrayList<>();
        double remanente = importeTotal;

        // Algoritmo de distribución del importe sobre las facturas
        for (Factura factura : facturas) {

            // Si ya no queda dinero, cortamos
            if (remanente < EPSILON) break;

            double saldoActual = factura.calcularSaldoPendiente();

            // Si la factura ya está al día, seguimos con la siguiente
            if (saldoActual < EPSILON) continue;

            // Monto que se va a pagar en esta factura
            double montoAPagar = Math.min(saldoActual, remanente);

            // Creamos el pago asociado a la factura
            Pago nuevoPago = Pago.builder()
                    .importe(montoAPagar)
                    .metodoPago(metodoPago)
                    .empleadoResponsable(empleadoResponsable)
                    .observaciones(observaciones)
                    .factura(factura)
                    .build();

            pagosAInsertar.add(nuevoPago);

            // Actualizamos estado de la factura según el nuevo saldo
            double nuevoSaldo = saldoActual - montoAPagar;
            if (nuevoSaldo < EPSILON) {
                factura.setEstado(EstadoFactura.PAGADA);
            } else {
                factura.setEstado(EstadoFactura.PARCIAL);
            }

            remanente -= montoAPagar;
        }

        // Guardamos todos los pagos generados
        return pagoRepositorio.saveAll(pagosAInsertar);
    }

    // PAGO INDIVIDUAL + EMISIÓN DE RECIBO 
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
        //    idRecibo será Long autoincremental (IDENTITY)
        Recibo recibo = new Recibo();
        recibo.setCliente(factura.getCliente());
        // importeTotal lo seteamos más abajo cuando tengamos el detalle
        recibo.setImporteTotal(0.0);

        // Persistimos el recibo primero para que tenga id_recibo y nro_recibo
        Recibo reciboPersistido = reciboRepositorio.save(recibo);

        // 4) Crear el PAGO asociado a la factura Y al recibo
        Pago pago = Pago.builder()
                .importe(importe)
                .metodoPago(metodoPago)
                .empleadoResponsable(empleadoResponsable)
                .observaciones(observaciones)
                .factura(factura)
                .recibo(reciboPersistido)   // vínculo con el recibo
                .build();

        // Asociar el pago a la factura en memoria 
        factura.agregarPago(pago);

        // Guardamos el pago
        pagoRepositorio.save(pago);

        // 5) Actualizar el estado de la factura según el nuevo saldo
        double saldoPendienteDespues = factura.calcularSaldoPendiente();

        if (Math.abs(saldoPendienteDespues) < 0.01) {
            factura.setEstado(EstadoFactura.PAGADA);
        } else if (saldoPendienteDespues < factura.getPrecioTotal()) {
            factura.setEstado(EstadoFactura.PARCIAL);
        }

        facturaRepositorio.save(factura);

        // 6) Crear el DETALLE DEL RECIBO basado en esta factura y el pago
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setRecibo(reciboPersistido);
        detalle.setFactura(factura);
        detalle.setImporteAplicado(importe);
        detalle.setSaldoPendienteFactura(saldoPendienteDespues);

        // Asociar detalle al recibo (si Recibo tiene lista de detalles)
        if (reciboPersistido.getDetalles() == null) {
            reciboPersistido.setDetalles(new ArrayList<>());
        }
        reciboPersistido.getDetalles().add(detalle);

        // 7) Actualizar el importe total del recibo
        reciboPersistido.setImporteTotal(importe);

        // 8) Guardar nuevamente el recibo con su detalle
        Recibo reciboFinal = reciboRepositorio.save(reciboPersistido);

        // 9) Devolver el recibo completo para que el controlador lo muestre/descargue
        return reciboFinal;
    }

    // MÉTODOS DE CONSULTA

    public Optional<Pago> buscarPorId(Long id) {
        return pagoRepositorio.findById(id);
    }

    public List<Pago> listarPagosPorFactura(Long idFactura) {
        return pagoRepositorio.findByFacturaIdFactura(idFactura);
    }

    public List<Pago> listarPagosPorCliente(Long idCliente) {
        return pagoRepositorio.findByClienteId(idCliente);
    }

    public Double calcularTotalPagadoPorFactura(Long idFactura) {
        return pagoRepositorio.calcularTotalPagadoPorFactura(idFactura);
    }
}
