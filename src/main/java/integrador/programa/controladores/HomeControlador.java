package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ClienteServicio;
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

@Controller
public class HomeControlador extends Object {

    // instancias de servicio

    @Autowired
    private ClienteServicio clienteServicio;

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
            // 1. Busca el cliente principal
            Cliente cliente = clienteServicio.buscarPorId(id);
            model.addAttribute("cliente", cliente);

            // lista x si no tenemos servicios cargados
            // List<AsignacionServicio> servicios = asignacionServicio.buscarPorCliente(id);
            // model.addAttribute("servicios", servicios);

            List<Cliente> subClientes = clienteServicio.listarTodos();
            subClientes.removeIf(c -> c.getIdCuenta().equals(id));
            model.addAttribute("subClientes", subClientes);

            model.addAttribute("servicios", Collections.emptyList());

            return "gestion-clientes-detalle";

        } catch (Exception e) {
            return "redirect:/clientes";
        }
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
            @RequestParam(value = "serviciosIds", required = false) List<String> serviciosIds,
            @RequestParam("mes") String mes,
            Model model
    ) {
        // asegura que siempre se cargue la lista de servicios
        List<Servicio> misServicios = servicioServicio.listarTodos();
        model.addAttribute("servicios", misServicios);

        if (serviciosIds == null || serviciosIds.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos un servicio para la facturación masiva.");
            return "facturacion-masiva";
        }

        try {
            int mesInt = Integer.parseInt(mes);
            int anioInt = LocalDate.now().getYear();
            //int anioInt = Integer.parseInt();
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
            System.err.println("[ERROR] procesarFacturacionMasivaFormulario failed: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error al procesar la facturación masiva: " + e.getMessage());
            return "facturacion-masiva";
        }
    }

}
