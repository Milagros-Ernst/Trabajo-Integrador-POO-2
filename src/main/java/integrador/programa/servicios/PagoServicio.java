package integrador.programa.servicios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.repositorios.PagoRepositorio;
import integrador.programa.repositorios.FacturaRepositorio;
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

    public PagoServicio(PagoRepositorio pagoRepositorio, FacturaRepositorio facturaRepositorio) {
        this.pagoRepositorio = pagoRepositorio;
        this.facturaRepositorio = facturaRepositorio;
    }

    @Transactional
    public List<Pago> registrarPagoMasivo(List<Long> idsFacturas, Double importeTotal, MetodoPago metodoPago,
                                          String empleadoResponsable, String observaciones) {

        // 1. Validaciones iniciales
        if (importeTotal <= 0) {
            throw new IllegalArgumentException("El importe debe ser mayor a 0.");
        }

        // 2. Obtener facturas
        List<Factura> facturas = facturaRepositorio.findAllById(idsFacturas);
        if (facturas.size() != idsFacturas.size()) {
            throw new IllegalArgumentException("No se encontraron todas las facturas solicitadas.");
        }

        // 3. Ordenar: Pagar primero las más antiguas (por fecha de la factura)
        // Asumo que tu entidad Factura tiene getFecha(). Si usas vencimiento, cambia a getVencimiento()
        facturas.sort(Comparator.comparing(Factura::getFecha));

        // 4. Calcular deuda total del grupo seleccionado
        double deudaTotal = facturas.stream()
                .mapToDouble(Factura::calcularSaldoPendiente)
                .sum();

        // Pequeño margen de error para comparaciones de punto flotante
        double EPSILON = 0.001;

        if (importeTotal > deudaTotal + EPSILON) {
            throw new IllegalArgumentException(
                    String.format("El importe ($%.2f) excede la deuda total seleccionada ($%.2f)",
                            importeTotal, deudaTotal));
        }

        List<Pago> pagosAInsertar = new ArrayList<>();
        double remanente = importeTotal;

        // 5. Algoritmo de Distribución (Cascada)
        for (Factura factura : facturas) {
            // Si se acabó el dinero, terminamos
            if (remanente < EPSILON) break;

            double saldoActual = factura.calcularSaldoPendiente();

            // Si la factura ya estaba pagada por alguna razón, saltar
            if (saldoActual < EPSILON) continue;

            // Definir cuánto pagamos a ESTA factura
            double montoAPagar = Math.min(saldoActual, remanente);

            // Crear el Pago
            Pago nuevoPago = Pago.builder()
                    .importe(montoAPagar)
                    .metodoPago(metodoPago)
                    .empleadoResponsable(empleadoResponsable)
                    .observaciones(observaciones)
                    .factura(factura)
                    .build();

            pagosAInsertar.add(nuevoPago);

            // --- ACTUALIZACIÓN DE ESTADO DE LA FACTURA ---
            double nuevoSaldo = saldoActual - montoAPagar;

            if (nuevoSaldo < EPSILON) {
                // Se pagó completo
                factura.setEstado(EstadoFactura.PAGADA);
            } else {
                // Queda saldo pendiente
                factura.setEstado(EstadoFactura.PARCIAL);
            }
            remanente -= montoAPagar;
        }

        return pagoRepositorio.saveAll(pagosAInsertar);
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
