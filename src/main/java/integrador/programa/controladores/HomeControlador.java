package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.ServicioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import integrador.programa.servicios.FacturaServicio;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Set;
import integrador.programa.servicios.ClienteServicioServicio;

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
        List<Cliente> misClientes = clienteServicio.listarTodos();

        model.addAttribute("clientes", misClientes);
        return "gestion-clientes-inicio";
    }

    @GetMapping("/servicios")
    public String irAServicios(Model model) {

        List<Servicio> misServicios = servicioServicio.listarTodos();

        model.addAttribute("servicios", misServicios);

        return "gestion-servicio-abm";
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

            List<Cliente> subClientes = clienteServicio.listarTodos();
            subClientes.removeIf(c -> c.getIdCuenta().equals(id));
            model.addAttribute("subClientes", subClientes);

            model.addAttribute("servicios", Collections.emptyList());

            return "gestion-clientes-detalle";

        } catch (Exception e) {
            return "redirect:/clientes";
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
            @RequestParam(value = "serviciosIds", required = true) List<String> serviciosIds,
            @RequestParam("mes") String mes,
            Model model
    ) {
        // asegura que siempre se cargue la lista de servicios
        List<Servicio> misServicios = servicioServicio.listarTodos();
        model.addAttribute("servicios", misServicios);

        /*if (serviciosIds == null || serviciosIds.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un servicio para la facturación masiva.");
            return "facturacion-masiva";
        }*/

        try {
            int mesInt = Integer.parseInt(mes);
            int anioInt = LocalDate.now().getYear();
            java.time.YearMonth ym = java.time.YearMonth.of(anioInt, mesInt);
            java.time.Month periodo = java.time.Month.of(mesInt);
            java.time.LocalDate fechaVencimiento = ym.atEndOfMonth();

            // Llamada al servicio que genera las facturas masivas y devuelve un registro
            integrador.programa.modelo.LogFacturacionMasiva registro = null;
            try {
                registro = facturaServicio.emitirFacturaMasiva(serviciosIds, periodo, fechaVencimiento);
            } catch (Exception inner) {
                System.err.println("[ERROR] Error executing emitirFacturaMasiva: " + inner.getMessage());
                inner.printStackTrace();
                throw inner;
            }

            return "facturacion-masiva";
        } catch (Exception e) {
            return "facturacion-masiva";
        }
    }

    // ver si mato la carga de servicios acá.
    @GetMapping("/facturacion/individual")
    public String irAFacturacionIndividual(
            @RequestParam(value = "clienteId", required = false) Long clienteId,
            Model model
    ) {
        try {
            List<Servicio> misServicios = servicioServicio.listarTodos();
            model.addAttribute("servicios", misServicios);

            if (clienteId != null) {
                try {
                    Cliente clienteEncontrado = clienteServicio.buscarPorId(clienteId);
                    model.addAttribute("cliente", clienteEncontrado);
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
            @RequestParam("clienteIdForm") Long clienteId, // ID del cliente que viene del input oculto
            @RequestParam("mes") String mes,
            Model model
    ) {
        List<Servicio> misServicios = servicioServicio.listarTodos();
        model.addAttribute("servicios", misServicios);
        Cliente clienteEncontrado = null;
        try {
             clienteEncontrado = clienteServicio.buscarPorId(clienteId);
             model.addAttribute("cliente", clienteEncontrado);
        } catch (Exception e) {
             model.addAttribute("errorCliente", "Error al recuperar el cliente.");
             return "facturacion-individual"; // Vuelve con error
        }
        
        // Validar que se seleccionó al menos un servicio
        if (serviciosIds == null || serviciosIds.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un servicio para facturar.");
            return "facturacion-individual";
        }

        try {
            // ... (Aquí iría para llamar a facturaServicio.emitirFacturaINDIVIDUAL(...)) ...
            // ... Esta lógica necesita ser creada en tu FacturaServicio ...

            // Simulación de éxito
            int mesInt = Integer.parseInt(mes);
            model.addAttribute("success", "Factura generada exitosamente para el cliente " + clienteEncontrado.getNombre() + " para el mes " + mesInt);
            return "facturacion-individual";

        } catch (Exception e) {
            model.addAttribute("error", "Error al generar la factura: " + e.getMessage());
            return "facturacion-individual";
        }
    }


}
