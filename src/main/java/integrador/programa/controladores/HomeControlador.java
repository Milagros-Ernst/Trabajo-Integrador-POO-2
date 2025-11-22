package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.LogFacturacionMasiva;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.ServicioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import integrador.programa.servicios.FacturaServicio;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeControlador extends Object {

    // instancias de servicio

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private ClienteServicioServicio clienteServicioServicio;

    @Autowired
    private ServicioServicio servicioServicio;

    @Autowired
    private FacturaServicio facturaServicio;
    

    @GetMapping("/")
    public String mostrarPaginaInicio(){
        return "inicio";
    }

    @GetMapping("/clientes")
    public String irAClientes(Model model) {
        List<Cliente> misClientes = clienteServicio.listarClientesActivos();

        model.addAttribute("clientes", misClientes);
        return "gestion-clientes-inicio";
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> crearCliente(@RequestBody Cliente nuevoCliente) {
        try {
            nuevoCliente.setEstadoCuenta(EstadoCuenta.ACTIVA);
            Cliente clienteGuardado = clienteServicio.crearCliente(nuevoCliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(clienteGuardado);

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al dar de alta: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/servicios")
    public String irAServicios(Model model) {

        List<Servicio> misServicios = servicioServicio.listarTodos();

        model.addAttribute("servicios", misServicios);

        return "gestion-servicio-abm";
    }

    @PostMapping("/servicios")
    public ResponseEntity<?> crearServicio(@RequestBody Servicio nuevoServicio) {
        try {
            nuevoServicio.setEstadoServicio(EstadoServicio.ALTA);
            Servicio servicioGuardado = servicioServicio.agregarServicio(nuevoServicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(servicioGuardado);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    @PutMapping("/servicios/{id}")
    public ResponseEntity<?> modificarServicio(@PathVariable String id, @RequestBody Servicio servicioActualizado) {
        try {

            servicioServicio.actualizarServicio(id, servicioActualizado);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/servicios/{id}")
    public ResponseEntity<?> bajaServicio(@PathVariable String id) {
        try {
            servicioServicio.eliminarServicio(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @GetMapping("/facturacion")
    public String irAFacturacion() {
        return "facturacion-inicio";
    }

    @GetMapping("/clientes/{id}")
    public String irADetalleCliente(@PathVariable Long id, Model model) {

        try {
            Cliente cliente = clienteServicio.buscarPorId(id);
            model.addAttribute("cliente", cliente);

            List<Cliente> subClientes = clienteServicio.listarClientesActivos();
            subClientes.removeIf(c -> c.getIdCuenta().equals(id));
            model.addAttribute("subClientes", subClientes);

            List<integrador.programa.modelo.ClienteServicio> contratados = clienteServicioServicio.listarServiciosActivosDeCliente(id);
            model.addAttribute("serviciosContratados", contratados);

            return "gestion-clientes-detalle";

        } catch (Exception e) {
            return "redirect:/clientes";
        }
    }

    @PutMapping("/clientes/{id}")
    public ResponseEntity<?> modificarCliente(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        try {
            clienteServicio.actualizarCliente(id, clienteActualizado);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<?> bajaCliente(@PathVariable Long id) {
        try {
            clienteServicio.eliminarCliente(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Error al dar de baja: " + e.getMessage());
        }
    }


    @GetMapping("/clientes/{id}/asignar")
    public String irAAsignarServicios(@PathVariable Long id, Model model) {
        try {
            Cliente cliente = clienteServicio.buscarPorId(id);
            model.addAttribute("cliente", cliente);

            List<integrador.programa.modelo.ClienteServicio> contratados = clienteServicioServicio.listarServiciosActivosDeCliente(id);
            model.addAttribute("serviciosContratados", contratados);

            List<Servicio> todosLosServicios = servicioServicio.listarTodos();

            Set<String> idsServiciosContratados = contratados.stream()
                    .map(asignacion -> asignacion.getServicio().getIdServicio())
                    .collect(Collectors.toSet());

            List<Servicio> disponibles = todosLosServicios.stream()
                    .filter(servicio -> !idsServiciosContratados.contains(servicio.getIdServicio()))
                    .collect(Collectors.toList());

            model.addAttribute("serviciosDisponibles", disponibles);

            return "gestion-clientes-asignserv";

        } catch (Exception e) {

            System.err.println("Error en irAAsignarServicios: " + e.getMessage());
            return "redirect:/clientes";
        }
    }

    @PostMapping("/clientes/{id}/asignar")
    public String asignarServicioACliente(@PathVariable Long id,
                                          @RequestParam String servicioId) {

        try {
            clienteServicioServicio.asignarServicioACliente(id, servicioId);

        } catch (Exception e) {
            System.out.println("Error al asignar servicio: " + e.getMessage());
        }

        return "redirect:/clientes/" + id + "/asignar";
    }


    @GetMapping("facturacion/masiva")
    public String irAFacturacionMasiva(Model model) {
        // Añade la lista de servicios al modelo para que la plantilla los muestre
        List<Servicio> misServicios = servicioServicio.listarTodos();
        model.addAttribute("servicios", misServicios);
        return "facturacion-masiva";
    }

    @PostMapping("/facturacion/masiva")
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


    @GetMapping("/facturacion/individual")
    public String irAFacturacionIndividual(
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            Model model)
    {
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

    @PostMapping("/facturacion/individual")
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

    @PostMapping("/clientes/{id}/dar-de-baja")
    public String darDeBajaCliente(@PathVariable Long id) {
        try {
            clienteServicio.BajaCliente(id);
            return "redirect:/clientes";
        } catch (Exception e) {
            return "redirect:/clientes";
        }
    }
}
