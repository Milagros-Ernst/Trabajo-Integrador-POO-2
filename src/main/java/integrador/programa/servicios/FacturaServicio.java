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
    public Factura emitirFacturaIndividual(
            Long idCliente,
            int periodo,
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
        final int ACTUAL = 1; // 
        final int LONGITUD_SERIE = 8; 

        String ultimoNroSerie = facturaRepositorio.findMaxNroSerieByTipo(tipo);
        long numeroActual = 0;
        if (ultimoNroSerie != null) {
            try {
                String[] partes = ultimoNroSerie.split("-");
                if (partes.length == 2) {
                    // separamos en dos y pasamos a numero (ej: "00000001" es 1)
                    numeroActual = Long.parseLong(partes[1]);
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Advertencia: Error al parsear el número de serie. Iniciando desde 0. Error: " + e.getMessage());
                numeroActual = 0;
            }
        }

        long proximoNumero = numeroActual + 1;
        String puntoActual = String.format("%04d", ACTUAL); //  1 -> "0001"
        
        String numeroSerieStr = String.format("%0" + LONGITUD_SERIE + "d", proximoNumero);
        return puntoActual + "-" + numeroSerieStr;
    }


    
    private double calcularPrecioProporcional(double precioUnitario, LocalDate fechaInicio, int periodo) {
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
