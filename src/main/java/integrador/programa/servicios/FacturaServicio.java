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
import integrador.programa.modelo.enumeradores.TipoComprobante; // Asegúrate que esté importado

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
    private ClienteServicioServicio clienteServicioServicio; 
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

    public Optional<Factura> buscarPorId(Long id) {
        return facturaRepositorio.findById(id);
    }

    public List<Factura> buscarFacturasPorCliente(Cliente cliente) {
        return facturaRepositorio.findByCliente(cliente);
    }

    @Transactional
    public NotaCredito bajaFactura(Long idFactura, String motivoAnulacion) {
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


    @Transactional
    public Factura emitirFacturaIndividual(
            Cliente cliente,
            List<String> serviciosIds,
            int periodo,
            java.time.LocalDate fechaVencimiento
    ) throws Exception {

        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setPeriodo(periodo);
        factura.setFecha(java.time.LocalDate.now());
        factura.setVencimiento(fechaVencimiento);
        factura.setEstado(EstadoFactura.VIGENTE); 
        factura.setEmpleadoResponsable(RESPONSABLE); 
        
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
            
            DetalleFactura detalle = new DetalleFactura(); 
            
            detalle.setFactura(factura);
            detalle.setServicio(servicioAFacturar);
            detalle.setDescripcion(servicioAFacturar.getDescripcion());
            detalle.setPrecio(servicioAFacturar.getPrecioUnitario().intValue());
            
            detalles.add(detalle);
        }

        factura.setDetalles(detalles);
        factura.calcularTotal(); 
        
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
            
            List<String> idsParaEsteCliente = new ArrayList<>(idServiciosFacturar);

            try {
                List<integrador.programa.modelo.ClienteServicio> serviciosDelCliente = 
                    clienteServicioServicio.listarServiciosActivosDeCliente(cliente.getIdCuenta());
                
                List<String> idsServiciosDelCliente = serviciosDelCliente.stream()
                    .map(cs -> cs.getServicio().getIdServicio())
                    .collect(Collectors.toList());
                
                idsParaEsteCliente.retainAll(idsServiciosDelCliente);
                
            } catch (Exception e) {
                System.err.println("Error obteniendo servicios para cliente " + cliente.getIdCuenta() + ": " + e.getMessage());
                idsParaEsteCliente.clear(); // No facturar si no podemos verificar
            }

            if (!idsParaEsteCliente.isEmpty()) {
                try {

                    emitirFacturaIndividual(
                        cliente,             
                        idsParaEsteCliente,  
                        periodo,             
                        fechaVencimiento     
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