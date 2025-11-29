package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Pago;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.PagoServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteControlador {

    private final ClienteService clienteService;
    private final ClienteServicioServicio clienteServicioServicio;



    public ClienteControlador(ClienteService clienteService,
                              ClienteServicioServicio clienteServicioServicio, FacturaServicio facturaServicio, PagoServicio pagoServicio) {
        this.clienteService = clienteService;
        this.clienteServicioServicio = clienteServicioServicio;
    }
    //metodo para ir al inicio de clientes
    @GetMapping
    public String irAInicioClientes() {
        return "clientes-inicio";
    }
// metodo para ir a la pantalla de gestion
    @GetMapping("/gestion")
    public String irAClientes(Model model) {
        List<Cliente> misClientes = clienteService.listarClientesActivos();
        model.addAttribute("clientes", misClientes);
        model.addAttribute("cliente", new Cliente());
        return "gestion-clientes-inicio";
    }

    // metodos para la gestión de clientes
    @PostMapping("/gestion")
    public String crearCliente(@ModelAttribute Cliente nuevoCliente, Model model) {
        try {
            nuevoCliente.setEstadoCuenta(EstadoCuenta.ACTIVA);
            clienteService.crearCliente(nuevoCliente);
            return "redirect:/clientes/gestion";

        } catch (Exception e) {
            model.addAttribute("error", "Error al crear cliente: " + e.getMessage());
            model.addAttribute("clientes", clienteService.listarClientesActivos());
            return "gestion-clientes-inicio";
        }
    }

    @GetMapping("/gestion/{id}")
    public String irADetalleCliente(@PathVariable Long id, Model model) {

        try {
            Cliente cliente = clienteService.buscarPorId(id);
            model.addAttribute("cliente", cliente);

            List<Cliente> subClientes = clienteService.listarClientesActivos();
            subClientes.removeIf(c -> c.getIdCuenta().equals(id));
            model.addAttribute("subClientes", subClientes);

            List<integrador.programa.modelo.ClienteServicio> contratados = clienteServicioServicio.listarServiciosActivosDeCliente(id);
            model.addAttribute("serviciosContratados", contratados);

            return "gestion-clientes-detalle";

        } catch (Exception e) {
            return "redirect:/clientes/gestion";
        }
    }

    @PutMapping("/gestion/{id}")
    public String modificarCliente(@PathVariable Long id, @ModelAttribute Cliente clienteActualizado) {
        try {
            clienteService.modificarCliente(id, clienteActualizado);
            return "redirect:/clientes/gestion" + id;
        } catch (Exception e) {
            return "redirect:/clientes/gestion" + id + "?error=" + e.getMessage();
        }
    }

    // si es una baja lógica, seria un post?
    @PostMapping("/gestion/{id}")
    public String darDeBajaCliente(@PathVariable Long id) {
        try {
            clienteService.bajaCliente(id);
            return "redirect:/clientes/gestion";
        } catch (Exception e) {
            return "redirect:/clientes/gestion";
        }
    }

    // reactivar cliente
    @PutMapping("/gestion/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente clienteReactivado = clienteService.reactivarCliente(id);
            return "redirect:/clientes/gestion" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", "No se pudo reactivar: " + e.getMessage());
            return "redirect:/clientes/gestion";
        }
    }


}

