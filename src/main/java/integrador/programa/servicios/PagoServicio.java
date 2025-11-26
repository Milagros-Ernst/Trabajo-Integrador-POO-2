package integrador.programa.servicios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.repositorios.PagoRepositorio;
import integrador.programa.repositorios.FacturaRepositorio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

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

    public Pago registrarPago(@Valid Pago pago, Long idFactura) {
        // buscar la factura
        Factura factura = facturaRepositorio.findById(idFactura)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la factura con ID: " + idFactura));
        
        // validar que el importe no sea mayor al saldo pendiente
        double saldoPendiente = factura.calcularSaldoPendiente();
        if (pago.getImporte() > saldoPendiente) {
            throw new IllegalArgumentException(
                String.format("El importe del pago (%.2f) excede el saldo pendiente (%.2f)", 
                    pago.getImporte(), saldoPendiente)
            );
        }
        // Asociar el pago a la factura
        pago.setFactura(factura);
        return pagoRepositorio.save(pago);
    }

    public Pago registrarPagoTotal(Long idFactura, MetodoPago metodoPago, String empleadoResponsable, String observaciones) {
        Factura factura = facturaRepositorio.findById(idFactura)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la factura con ID: " + idFactura));
        
        double saldoPendiente = factura.calcularSaldoPendiente();
        
        if (saldoPendiente <= 0) {
            throw new IllegalArgumentException("La factura ya está completamente pagada");
        }
        
        Pago pago = Pago.builder()
            .importe(saldoPendiente)
            .metodoPago(metodoPago)
            .empleadoResponsable(empleadoResponsable)
            .observaciones(observaciones)
            .factura(factura)
            .build();
        
        return pagoRepositorio.save(pago);
    }

    public Pago registrarPagoParcial(Long idFactura, Double importe, MetodoPago metodoPago, 
                                     String empleadoResponsable, String observaciones) {
        Factura factura = facturaRepositorio.findById(idFactura)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró la factura con ID: " + idFactura));
        
        double saldoPendiente = factura.calcularSaldoPendiente();
        
        if (importe > saldoPendiente) {
            throw new IllegalArgumentException(
                String.format("El importe del pago (%.2f) excede el saldo pendiente (%.2f)", 
                    importe, saldoPendiente)
            );
        }
        
        Pago pago = Pago.builder()
            .importe(importe)
            .metodoPago(metodoPago)
            .empleadoResponsable(empleadoResponsable)
            .observaciones(observaciones)
            .factura(factura)
            .build();
        
        return pagoRepositorio.save(pago);
    }

    // public List<Pago> listarPagos() {
    //     return pagoRepositorio.findAll();
    // }

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
