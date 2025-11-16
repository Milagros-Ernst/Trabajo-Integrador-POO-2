package integrador.programa.controladores;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeControlador extends Object {

    public HomeControlador() {

    }

    @GetMapping("/")
    public String mostrarPaginaInicio(){
        return "inicio";
    }

    @GetMapping("/clientes")
    public String irAClientes() {
        return "gestion-clientes-inicio";
    }

    @GetMapping("/servicios")
    public String irAServicios() {
        return "gestion-servicio-abm";
    }

    @GetMapping("/facturacion")
    public String irAFacturacion() {
        return "facturacion-inicio";
    }
}
