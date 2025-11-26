package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.ClienteServicioServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.util.List;

@Controller
@RequestMapping("/clientes")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;
    private final ClienteServicioServicio clienteServicioServicio;

    public ClienteControlador(ClienteServicio clienteServicio,
                              ClienteServicioServicio clienteServicioServicio) {
        this.clienteServicio = clienteServicio;
        this.clienteServicioServicio = clienteServicioServicio;
    }
// metodo para ir a la pantalla de gestion
    @GetMapping
    public String irAClientes(Model model) {
        List<Cliente> misClientes = clienteServicio.listarClientesActivos();
        model.addAttribute("clientes", misClientes);
        model.addAttribute("cliente", new Cliente());
        return "gestion-clientes-inicio";
    }

    // metodos para la gestión de clientes
    @PostMapping
    public String crearCliente(@ModelAttribute Cliente nuevoCliente, Model model) {
        try {
            nuevoCliente.setEstadoCuenta(EstadoCuenta.ACTIVA);
            clienteServicio.crearCliente(nuevoCliente);
            return "redirect:/clientes";

        } catch (Exception e) {
            model.addAttribute("error", "Error al crear cliente: " + e.getMessage());
            model.addAttribute("clientes", clienteServicio.listarClientesActivos());
            return "gestion-clientes-inicio";
        }
    }

    @GetMapping("/{id}")
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

    @PutMapping("/{id}")
    public String modificarCliente(@PathVariable Long id, @ModelAttribute Cliente clienteActualizado) {
        try {
            clienteServicio.modificarCliente(id, clienteActualizado);
            return "redirect:/clientes/" + id;
        } catch (Exception e) {
            return "redirect:/clientes/" + id + "?error=" + e.getMessage();
        }
    }

    // si es una baja lógica, seria un post?
    @PostMapping("/{id}")
    public String darDeBajaCliente(@PathVariable Long id) {
        try {
            clienteServicio.bajaCliente(id);
            return "redirect:/clientes";
        } catch (Exception e) {
            return "redirect:/clientes";
        }
    }

    // reactivar cliente
    @PutMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente clienteReactivado = clienteServicio.reactivarCliente(id);
            return "redirect:/clientes/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", "No se pudo reactivar: " + e.getMessage());
            return "redirect:/clientes";
        }
    }
}