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

import java.util.Collections;
import java.util.List;

@Controller
public class HomeControlador extends Object {

    // instancias de servicio

    @Autowired
    private ClienteServicio clienteServicio;

    @Autowired
    private ServicioServicio servicioServicio;

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

}
