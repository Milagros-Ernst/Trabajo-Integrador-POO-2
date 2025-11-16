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
import java.time.Month;
import java.time.temporal.ChronoUnit;
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
    public NotaCredito bajaFactura(String id) {
        Optional<Factura> opt = facturaRepositorio.findById(id);
        if (opt.isEmpty()) return null;
        Factura factura = opt.get();
        // marco factura como anulada y guardo
        factura.setEstado(EstadoFactura.ANULADA);
        facturaRepositorio.save(factura);
        NotaCredito nota = notaServicio.altaNotaPorFactura(factura);
        return nota;
    }



    // alerta de mucho texto. función para el alta.
    public Factura emitirFacturaIndividual(
            Long idCliente,
            Month periodo,
            LocalDate fechaVencimiento,
            Map<String, LocalDate> serviciosConFechaInicio 
    ) {
        
        Cliente cliente = clienteRepositorio.findById(idCliente)
            .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado."));
        
        if (cliente.getEstadoCuenta() == null || !cliente.getEstadoCuenta().equals(EstadoCuenta.ACTIVA)) { 
            throw new IllegalStateException("Solo se puede facturar a cuentas Activas.");
        }

        Factura nuevaFactura = new Factura();
        nuevaFactura.setCliente(cliente);
        nuevaFactura.setFecha(LocalDate.now()); 
        nuevaFactura.setVencimiento(fechaVencimiento);
        nuevaFactura.setEmpleadoResponsable(RESPONSABLE);
        nuevaFactura.setEstado(EstadoFactura.VIGENTE);
        nuevaFactura.setPeriodo(periodo);
        nuevaFactura.setTipo(TipoComprobante.A); // por defecto. lo cambiaría para recibir por pantalla
        
        nuevaFactura.setNroSerie(generarProximoNroSerie(TipoComprobante.A)); 

        for (Map.Entry<String, LocalDate> entry : serviciosConFechaInicio.entrySet()) {
            String idServicio = entry.getKey();
            LocalDate fechaInicio = entry.getValue();

            Servicio servicioModel = servicioRepositorio.findById(idServicio)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado: " + idServicio));
            
            double precioFinal = calcularPrecioProporcional(
                servicioModel.getPrecioUnitario(), 
                fechaInicio, 
                periodo
            );

            DetalleFactura detalle = new DetalleFactura();
            
            detalle.setDescripcion(servicioModel.getNombre());
            detalle.setPrecio((int)precioFinal);
            
            nuevaFactura.agregarDetalle(detalle);
        }

        nuevaFactura.calcularTotal(); 
        return facturaRepositorio.save(nuevaFactura);
    }
    
        private String generarProximoNroSerie(TipoComprobante tipo) {
        // por ahora hardcodeado
        return "0001-00000001";
    }

    private double calcularPrecioProporcional(double precioUnitario, LocalDate fechaInicio, Month periodo) {
        LocalDate inicioMes = LocalDate.of(fechaInicio.getYear(), periodo, 1);
        
        if (fechaInicio.isBefore(inicioMes) || fechaInicio.isEqual(inicioMes)) {
            return precioUnitario;
        }
        LocalDate finMes = inicioMes.plusMonths(1).minusDays(1);
        long diasDelMes = ChronoUnit.DAYS.between(inicioMes, finMes) + 1;
        long diasFacturar = ChronoUnit.DAYS.between(fechaInicio, finMes) + 1;

        double precioPorDia = precioUnitario / diasDelMes;
        return precioPorDia * diasFacturar;
    }



    public LogFacturacionMasiva emitirFacturaMasiva(
            List<String> idServiciosFacturar,
            Month periodo,
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
