package integrador.programa.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.DetalleFactura;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.LogFacturacionMasiva;
import integrador.programa.repositorios.ClienteRepositorio;
import integrador.programa.repositorios.FacturaRepositorio;
import integrador.programa.repositorios.LogFacturacionMasRepositorio;
import integrador.programa.repositorios.ServicioRepositorio;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.TipoComprobante;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacturaServicio {
    private static final String RESPONSABLE = "Admin Hardcodeado";

    @Autowired
    private FacturaRepositorio facturaRepositorio;
    @Autowired
    private NotaServicio notaServicio; 
    @Autowired
    private ClienteRepositorio clienteRepositorio; 
    @Autowired
    private ServicioServicio servicioServicio; 
    @Autowired
    private ServicioRepositorio servicioRepositorio;
    @Autowired
    private LogFacturacionMasRepositorio factMasivaRepositorio;
     
    public Factura agregarFactura(Factura factura) {
        return facturaRepositorio.save(factura);
    }

    public List<Factura> listarFacturas() {
        return facturaRepositorio.findAll();
    }

    public Optional<Factura> buscarPorId(String id) {
        return facturaRepositorio.findById(id);
    }

    @Transactional
    public NotaCredito bajaFactura(String idFactura, String motivoAnulacion) {
    Factura factura = facturaRepositorio.findById(idFactura)
        .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + idFactura));

    if (!factura.getEstado().equals(EstadoFactura.VIGENTE)) {
        throw new IllegalStateException("La factura no está VIGENTE y no puede ser anulada. Estado actual: " + factura.getEstado());
    }
    
    if (motivoAnulacion == null || motivoAnulacion.trim().isEmpty()) {
        throw new IllegalArgumentException("Debe proporcionar un motivo para la anulación.");
    }
    
    NotaCredito nota = notaServicio.altaNotaPorFactura(factura, motivoAnulacion);
    factura.setEstado(EstadoFactura.ANULADA);
    factura.setNotaCredito(nota); 
    facturaRepositorio.save(factura);
    return nota;
}



    // alerta de mucho texto. función para el alta.
@Transactional
    public Factura emitirFacturaIndividual(
            Cliente cliente,
            List<String> serviciosIds,
            int periodo, // Usamos 'int' para ser consistentes con tu método de masiva
            LocalDate fechaVencimiento
    ) throws Exception {

        // 1. Crear la Factura y setear datos básicos
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setPeriodo(periodo); // Guardamos el número del mes
        factura.setFecha(LocalDate.now());
        factura.setVencimiento(fechaVencimiento);
        factura.setEstado(EstadoFactura.VIGENTE);

        // 2. Determinar el Tipo de Comprobante (lógica de tu fact. masiva)
        switch (cliente.getCondIVA()) {
            case RESPONSABLE_INSCRIPTO:
                factura.setTipo(TipoComprobante.A);
                break;
            case MONOTRIBUTO:
            case CONSUMIDOR_FINAL:
            case EXENTO:
                factura.setTipo(TipoComprobante.B);
                break;
            default:
                factura.setTipo(TipoComprobante.C);
        }

        List<DetalleFactura> detalles = new ArrayList<>();
        for (String servicioId : serviciosIds) {
            Servicio servicioAFacturar = servicioServicio.buscarPorId(servicioId);

            DetalleFactura detalle = new DetalleFactura(servicioAFacturar, factura);
            
            detalles.add(detalle);
        }

        factura.setDetalles(detalles);
        factura.calcularTotal(); // Tu modelo Factura ya sabe cómo hacer esto
        
        return facturaRepositorio.save(factura);
    }


@Transactional
    public LogFacturacionMasiva emitirFacturaMasiva(
            List<String> idServiciosFacturar,
            int periodo,
            LocalDate fechaVencimiento
    ) {
        if (idServiciosFacturar == null || idServiciosFacturar.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un servicio para la facturación masiva.");
        }
        
        List<Servicio> serviciosFacturar = idServiciosFacturar.stream()
                .map(id -> servicioRepositorio.findById(id).orElse(null))
                .filter(s -> s != null)
                .collect(Collectors.toList());


        List<Cliente> clientesActivos = clienteRepositorio.findAll().stream()
                .filter(c -> c.getEstadoCuenta().equals(EstadoCuenta.ACTIVA))
                .collect(Collectors.toList());

        int facturasGeneradas = 0;

        for (Cliente cliente : clientesActivos) {
            LocalDate fechaInicio = LocalDate.of(LocalDate.now().getYear(), periodo, 1);
    
            Map<String, LocalDate> serviciosConFechaInicio = new HashMap<>();


            for (Servicio servicio : serviciosFacturar) {
                serviciosConFechaInicio.put(servicio.getIdServicio(), fechaInicio); 
            }

            if (!serviciosConFechaInicio.isEmpty()) {
                
                try {
                    emitirFacturaIndividual(
                        cliente.getIdCuenta(), 
                        periodo, 
                        fechaVencimiento, 
                        serviciosConFechaInicio
                    );
                    facturasGeneradas++;
                } catch (Exception e) {
                    System.err.println("Error al facturar cliente " + cliente.getIdCuenta() + ": " + e.getMessage());
                }
            }
        }

        String idsServiciosStr = String.join(",", idServiciosFacturar);
        
        LogFacturacionMasiva registro = new LogFacturacionMasiva(
            periodo,
            facturasGeneradas,
            idsServiciosStr,
            RESPONSABLE
        );
        
        return factMasivaRepositorio.save(registro);
    }
}
