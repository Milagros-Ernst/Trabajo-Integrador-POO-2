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

    public Optional<Factura> buscarPorId(String id) {
        return facturaRepositorio.findById(id);
    }

    @Transactional
    public NotaCredito bajaFactura(String idFactura, String motivoAnulacion) {
        Factura factura = facturaRepositorio.findById(idFactura)
            .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada con ID: " + idFactura));

        // CORRECCIÓN: Tu enum es VIGENTE, no PENDIENTE
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
        factura.setEstado(EstadoFactura.VIGENTE); // El estado PENDIENTE no existe en tu enum
        factura.setEmpleadoResponsable(RESPONSABLE); // Usamos la constante
        factura.setNroSerie("F-001"); // Hardcodeado
        
        // --- INICIO DE CORRECCIÓN (Enums) ---
        switch (cliente.getCondIVA()) {
            case RESPONSABLE_INSCRIPTO:
                factura.setTipo(TipoComprobante.A); // Era 'A'
                break;
            case MONOTRIBUTO:
            case CONSUMIDOR_FINAL:
            case EXENTO:
                factura.setTipo(TipoComprobante.B); // Era 'B'
                break;
            default:
                factura.setTipo(TipoComprobante.C); // Era 'C'
        }
        // --- FIN DE CORRECCIÓN ---

        List<DetalleFactura> detalles = new ArrayList<>();
        for (String servicioId : serviciosIds) {
            Servicio servicioAFacturar = servicioServicio.buscarPorId(servicioId);
            
            DetalleFactura detalle = new DetalleFactura(); 
            
            detalle.setFactura(factura);
            detalle.setServicio(servicioAFacturar);
            detalle.setDescripcion(servicioAFacturar.getDescripcion());
            // Tu modelo DetalleFactura espera un 'int' para precio
            detalle.setPrecio(servicioAFacturar.getPrecioUnitario().intValue());
            
            detalles.add(detalle);
        }

        factura.setDetalles(detalles);
        factura.calcularTotal(); // Esto usa la lógica que corregimos en Factura.java
        
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
        
        // Esta lógica está bien, trae los objetos Servicio
        List<Servicio> serviciosFacturar = idServiciosFacturar.stream()
                .map(id -> servicioRepositorio.findById(id).orElse(null))
                .filter(s -> s != null)
                .collect(Collectors.toList());

        // Trae clientes activos
        List<Cliente> clientesActivos = clienteRepositorio.findAll().stream()
                .filter(c -> c.getEstadoCuenta().equals(EstadoCuenta.ACTIVA))
                .collect(Collectors.toList());

        int facturasGeneradas = 0;

        for (Cliente cliente : clientesActivos) {
            
            // Reutilizamos la lista de IDs de servicios que recibimos
            // (La lógica anterior de crear un Map era innecesaria)
            List<String> idsParaEsteCliente = new ArrayList<>(idServiciosFacturar);

            // Filtramos servicios a los que el cliente SÍ está suscripto
            // (Esta es la lógica que faltaba)
            try {
                List<integrador.programa.modelo.ClienteServicio> serviciosDelCliente = 
                    clienteServicioServicio.listarServiciosActivosDeCliente(cliente.getIdCuenta());
                
                List<String> idsServiciosDelCliente = serviciosDelCliente.stream()
                    .map(cs -> cs.getServicio().getIdServicio())
                    .collect(Collectors.toList());
                
                // Dejamos en la lista solo los servicios que estén EN AMBAS listas
                idsParaEsteCliente.retainAll(idsServiciosDelCliente);
                
            } catch (Exception e) {
                System.err.println("Error obteniendo servicios para cliente " + cliente.getIdCuenta() + ": " + e.getMessage());
                idsParaEsteCliente.clear(); // No facturar si no podemos verificar
            }

            if (!idsParaEsteCliente.isEmpty()) {
                try {
                    // --- INICIO DE CORRECCIÓN (Llamada al método) ---
                    // Armamos los argumentos que el método SÍ espera
                    emitirFacturaIndividual(
                        cliente,             // Arg 1: El objeto Cliente
                        idsParaEsteCliente,  // Arg 2: La List<String> de IDs
                        periodo,             // Arg 3: El int del periodo
                        fechaVencimiento     // Arg 4: El LocalDate de vencimiento
                    );
                    // --- FIN DE CORRECCIÓN ---
                    
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