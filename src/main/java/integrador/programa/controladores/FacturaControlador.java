package integrador.programa.controladores;

import integrador.programa.servicios.ClienteServicioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.ServicioServicio;
import jakarta.validation.Valid;
import integrador.programa.modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("/facturacion")
public class FacturaControlador {

    @Autowired
    private FacturaServicio facturaServicio;
    @Autowired 
    private ClienteServicio clienteServicio; 
    @Autowired 
    private ServicioServicio servicioServicio;
    @Autowired
    private ClienteServicioServicio clienteServicioServicio;

    public FacturaControlador(FacturaServicio facturaServicio){
        this.facturaServicio = facturaServicio;
    }

    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaServicio.listarFacturas());
    }

    @GetMapping
    public String irAFacturacion() {
        return "facturacion-inicio";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtener(@PathVariable Long id) {
    return facturaServicio.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody Factura factura) {
        Factura saved = facturaServicio.agregarFactura(factura);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/alta") 
    public ResponseEntity<?> emitirFactura(@RequestBody Map<String, Object> requestBody) {         
        try {
            Long idCliente = ((Number)requestBody.get("idCliente")).longValue(); 
            
            int periodo = Integer.valueOf((String) requestBody.get("periodo"));
            LocalDate fechaVencimiento = LocalDate.parse((String) requestBody.get("fechaVencimiento"));
            
            @SuppressWarnings("unchecked") 
            Map<String, LocalDate> serviciosConFechaInicio = (Map<String, LocalDate>) requestBody.get("serviciosConFechaInicio"); 

            if (serviciosConFechaInicio == null || serviciosConFechaInicio.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe incluir al menos un servicio para facturar.");
            }

            Cliente clienteAFacturar = clienteServicio.buscarPorId(idCliente);
            List<String> serviciosIds = new ArrayList<>(serviciosConFechaInicio.keySet());

            Factura nuevaFactura = facturaServicio.emitirFacturaIndividual(
                clienteAFacturar,   
                serviciosIds,       
                periodo,            
                fechaVencimiento    
            );
            
            
            // si funciona
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);

        } catch (NoSuchElementException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado con ID: " + requestBody.get("idCliente"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno o formato de datos incorrecto: " + e.getMessage());
        }
    }

    @PostMapping("/alta/masiva") 
    public ResponseEntity<?> emitirFacturaMasiva(@RequestBody Map<String, Object> requestBody) {
        try {
            @SuppressWarnings("unchecked") 
            List<String> idServiciosFacturar = (List<String>) requestBody.get("idServicios");
            int periodo = Integer.valueOf((String) requestBody.get("periodo"));
            LocalDate fechaVencimiento = LocalDate.parse((String) requestBody.get("fechaVencimiento"));

            if (idServiciosFacturar == null || idServiciosFacturar.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe seleccionar al menos un servicio.");
            }
            
            LogFacturacionMasiva registro = facturaServicio.emitirFacturaMasiva(
                idServiciosFacturar, 
                periodo, 
                fechaVencimiento
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno o formato de datos incorrecto: " + e.getMessage());
        }
    }

    @PostMapping("/anular/{id}")
    public String anularFactura(@PathVariable Long id, 
                                @RequestParam("motivo") String motivo) {
        Long idCliente = null;
        try {
            Factura factura = facturaServicio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
            
            idCliente = factura.getCliente().getIdCuenta();

            facturaServicio.bajaFactura(id, motivo);

            return "redirect:/clientes/" + idCliente + "/facturacion";

        } catch (Exception e) {
            if (idCliente != null) {
                return "redirect:/clientes/" + idCliente + "/facturacion?error=" + e.getMessage();
            }
            return "redirect:/clientes"; 
        }
    }

    //métodos para la facturación - agrego del home

    @GetMapping("/masiva")
    public String irAFacturacionMasiva(Model model) {
        // Añade la lista de servicios al modelo para que la plantilla los muestre
        List<Servicio> misServicios = servicioServicio.listarTodos();
        model.addAttribute("servicios", misServicios);
        return "facturacion-masiva";
    }

    @PostMapping("/masiva")
    public String procesarFacturacionMasivaFormulario(
            @RequestParam("periodo") int periodo,
            @RequestParam("fechaVencimiento") LocalDate fechaVencimiento,
            @RequestParam(name = "idServiciosFacturar", required = false) List<String> idServiciosFacturar,
            Model model) {

        try {

            if (idServiciosFacturar == null || idServiciosFacturar.isEmpty()) {

                throw new IllegalArgumentException("Debe seleccionar al menos un servicio para facturar.");

            }
            LogFacturacionMasiva registro = facturaServicio.emitirFacturaMasiva(
                    idServiciosFacturar,
                    periodo,
                    fechaVencimiento
            );

            model.addAttribute("success",
                    "Facturación masiva completada. Se generaron " +
                            registro.getCantidadFacturas() + " facturas.");

        } catch (Exception e) {
            // Si algo falla, envía un mensaje de error
            model.addAttribute("error",
                    "Error al procesar la facturación: " + e.getMessage());
        }

        // Vuelve a cargar los servicios para la tabla
        List<Servicio> servicios = servicioServicio.listarTodos();
        model.addAttribute("servicios", servicios);

        // Vuelve a mostrar la misma página
        return "facturacion-masiva";
    }


    @GetMapping("/individual")
    public String irAFacturacionIndividual(
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            Model model) {
        try {
            if (clienteId != null) {
                try {
                    Cliente clienteEncontrado = clienteServicio.buscarPorId(clienteId);
                    model.addAttribute("cliente", clienteEncontrado);

                    List<integrador.programa.modelo.ClienteServicio> serviciosAsignados = clienteServicioServicio.listarServiciosActivosDeCliente(clienteId);
                    model.addAttribute("serviciosAsignados", serviciosAsignados);

                } catch (Exception eCliente) {
                    model.addAttribute("errorCliente", "Cliente no encontrado con ID: " + clienteId);
                }
            }
            return "facturacion-individual";

        } catch (Exception eGeneral) {
            System.err.println("Error grave al cargar facturación individual: " + eGeneral.getMessage());
            eGeneral.printStackTrace();
            model.addAttribute("errorGeneral", "Error al cargar la lista de servicios. Contacte al administrador.");
            return "facturacion-individual";
        }
    }

    @PostMapping("/individual")
    public String procesarFacturacionIndividualFormulario(
            @RequestParam(value = "serviciosIds", required = false) List<String> serviciosIds,
            @RequestParam("clienteIdForm") Long clienteId,
            @RequestParam("mes") String mes,
            @RequestParam("fechaVencimiento") LocalDate fechaVencimiento,

            Model model
    ) {
        Cliente clienteEncontrado = null;
        List<integrador.programa.modelo.ClienteServicio> serviciosAsignados = null;
        try {
            clienteEncontrado = clienteServicio.buscarPorId(clienteId);
            model.addAttribute("cliente", clienteEncontrado);
            serviciosAsignados = clienteServicioServicio.listarServiciosActivosDeCliente(clienteId);
            model.addAttribute("serviciosAsignados", serviciosAsignados);
        } catch (Exception e) {
            model.addAttribute("errorCliente", "Error al recuperar el cliente.");
            return "facturacion-individual";
        }

        if (serviciosIds == null || serviciosIds.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un servicio para facturar.");
            return "facturacion-individual";
        }

        try {
            int mesInt = Integer.parseInt(mes);
            Factura facturaGenerada = facturaServicio.emitirFacturaIndividual(
                    clienteEncontrado,
                    serviciosIds,
                    mesInt,
                    fechaVencimiento
            );

            model.addAttribute("success", "Factura N°" + facturaGenerada.getIdFactura() + " generada exitosamente para " + clienteEncontrado.getNombre());
            model.addAttribute("serviciosAsignados", clienteServicioServicio.listarServiciosActivosDeCliente(clienteId));

            return "facturacion-individual";

        } catch (Exception e) {
            model.addAttribute("error", "Error al generar la factura: " + e.getMessage());
            e.printStackTrace();
            return "facturacion-individual";
        }
    }

}
