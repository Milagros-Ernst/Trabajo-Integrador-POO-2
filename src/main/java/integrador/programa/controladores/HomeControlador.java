package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.ServicioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
}
