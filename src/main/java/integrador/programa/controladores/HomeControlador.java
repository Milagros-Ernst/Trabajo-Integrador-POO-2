package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Pago;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class HomeControlador extends Object {

    // instancias de servicio

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ClienteServicioServicio clienteServicioServicio;

    @Autowired
    private ServicioServicio servicioServicio;

    private PagoServicio pagoServicio;

    @Autowired
    private FacturaServicio facturaServicio;

    private Model model;


    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio";
    }



}
