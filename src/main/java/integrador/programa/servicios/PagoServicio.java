package integrador.programa.servicios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Recibo;
import integrador.programa.modelo.factory.ReciboFactory;
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
    public List<Pago> registrarPagoMasivo(List<Long> idsFacturas, Double importeTotal, MetodoPago metodoPago,
                                          String empleadoResponsable, String observaciones) {

        if (importeTotal <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor a 0.");
        }

        // obtenemos las facturas desde el repositorio
        List<Factura> facturas = facturaRepositorio.findAllById(idsFacturas);
        if (facturas.size() != idsFacturas.size()) {
            throw new IllegalArgumentException("No se encontraron todas las facturas solicitadas.");
        }

        // hacemos un ordenamiento para que se paguen primero las facturas más viejas
        facturas.sort(Comparator.comparing(Factura::getFecha));

        // calculamos la deuda total a pagar
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

        // se realiza un algoritmo de distribución para el pago masivo
        for (Factura factura : facturas) {
            // verificamos que todavía haya dinero para pagar
            if (remanente < EPSILON) break;

            double saldoActual = factura.calcularSaldoPendiente();

            // si la factura, por alguna razon esta pagada, continúa a la siguiente
            if (saldoActual < EPSILON) continue;

            // aca se define cuánto se va a pagar
            double montoAPagar = Math.min(saldoActual, remanente);

            // llamamos al builder de pago para construir el pago
            Pago nuevoPago = Pago.builder()
                    .importe(montoAPagar)
                    .metodoPago(metodoPago)
                    .empleadoResponsable(empleadoResponsable)
                    .observaciones(observaciones)
                    .factura(factura)
                    .build();

            pagosAInsertar.add(nuevoPago);

            // actualizamos el estado de la factura - si la pagamos completa, cambia a PAGADA, sino a PARCIAL
            double nuevoSaldo = saldoActual - montoAPagar;
            if (nuevoSaldo < EPSILON) {
                factura.setEstado(EstadoFactura.PAGADA);
            } else {
                factura.setEstado(EstadoFactura.PARCIAL);
            }

            remanente -= montoAPagar;
        }

        return pagoRepositorio.saveAll(pagosAInsertar);
    }

    // Registra un pago sobre UNA factura y emite el recibo correspondiente.
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

        // 3) Crear el pago asociado a la factura
        Pago pago = Pago.builder()
                .importe(importe)
                .metodoPago(metodoPago)
                .empleadoResponsable(empleadoResponsable)
                .observaciones(observaciones)
                .factura(factura)
                .build();

        // agregar el pago a la factura (mantiene coherente la lista en memoria)
        factura.agregarPago(pago);

        // 4) Actualizar estado de la factura según el nuevo saldo pendiente
        double saldoPendienteDespues = factura.calcularSaldoPendiente();
        if (Math.abs(saldoPendienteDespues) < 0.01) {
            factura.setEstado(EstadoFactura.PAGADA);
        } else if (saldoPendienteDespues < factura.getPrecioTotal()) {
            factura.setEstado(EstadoFactura.PARCIAL);
        }

        // 5) Persistir factura y pago
        facturaRepositorio.save(factura);
        pagoRepositorio.save(pago);

        // 6) Crear el recibo a partir de este pago (usa Factory, patrón creacional)
        Recibo recibo = ReciboFactory.crearDesdePago(pago);

        // 7) Guardar el recibo (la BD generará el nro_recibo)
        Recibo reciboGuardado = reciboRepositorio.save(recibo);

        // 8) Devolver el recibo para que el controlador lo muestre / descargue
        return reciboGuardado;
    }

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
