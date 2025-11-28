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

        // es recomendable tener un margen de error para comparaciones de punto flotante
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
