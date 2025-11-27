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
    private final FacturaServicio facturaServicio;
    private final PagoServicio pagoServicio;


    public ClienteControlador(ClienteService clienteService,
                              ClienteServicioServicio clienteServicioServicio, FacturaServicio facturaServicio, PagoServicio pagoServicio) {
        this.clienteService = clienteService;
        this.clienteServicioServicio = clienteServicioServicio;
        this.facturaServicio = facturaServicio;
        this.pagoServicio = pagoServicio;
    }
// metodo para ir a la pantalla de gestion
    @GetMapping
    public String irAClientes(Model model) {
        List<Cliente> misClientes = clienteService.listarClientesActivos();
        model.addAttribute("clientes", misClientes);
        model.addAttribute("cliente", new Cliente());
        return "gestion-clientes-inicio";
    }

    // metodos para la gestión de clientes
    @PostMapping
    public String crearCliente(@ModelAttribute Cliente nuevoCliente, Model model) {
        try {
            nuevoCliente.setEstadoCuenta(EstadoCuenta.ACTIVA);
            clienteService.crearCliente(nuevoCliente);
            return "redirect:/clientes";

        } catch (Exception e) {
            model.addAttribute("error", "Error al crear cliente: " + e.getMessage());
            model.addAttribute("clientes", clienteService.listarClientesActivos());
            return "gestion-clientes-inicio";
        }
    }

    @GetMapping("/{id}")
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
            return "redirect:/clientes";
        }
    }

    @PutMapping("/{id}")
    public String modificarCliente(@PathVariable Long id, @ModelAttribute Cliente clienteActualizado) {
        try {
            clienteService.modificarCliente(id, clienteActualizado);
            return "redirect:/clientes/" + id;
        } catch (Exception e) {
            return "redirect:/clientes/" + id + "?error=" + e.getMessage();
        }
    }

    // si es una baja lógica, seria un post?
    @PostMapping("/{id}")
    public String darDeBajaCliente(@PathVariable Long id) {
        try {
            clienteService.bajaCliente(id);
            return "redirect:/clientes";
        } catch (Exception e) {
            return "redirect:/clientes";
        }
    }

    // reactivar cliente
    @PutMapping("/{id}/reactivar")
    public String reactivar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Cliente clienteReactivado = clienteService.reactivarCliente(id);
            return "redirect:/clientes/" + id;
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", "No se pudo reactivar: " + e.getMessage());
            return "redirect:/clientes";
        }
    }


    @GetMapping("/{clienteId}/facturacion")
    public String irHistorialFacturacion(@PathVariable Long clienteId, Model model) {
        try {
            Cliente cliente = clienteService.buscarPorId(clienteId);
            model.addAttribute("cliente", cliente);

            List<Factura> facturas = facturaServicio.buscarFacturasPorCliente(cliente);

            // ordenamiento para determinar la prioridad (para q en la tabla aparezcan las q tienen mas prioridad primerp)

            facturas.sort((f1, f2) -> {
                int prioridad1 = getPrioridadEstado(f1.getEstado());
                int prioridad2 = getPrioridadEstado(f2.getEstado());

                // se compara
                int resultado = Integer.compare(prioridad1, prioridad2);

                // si tienen la misma prioridad, la que tiene fecha de vencimiento mas vieja va arriba
                if (resultado == 0) {
                    return f1.getVencimiento().compareTo(f2.getVencimiento());
                }

                return resultado;
            });

            model.addAttribute("facturas", facturas);

            // ahora enlistamos los pagos para la tabla de pagos

            List<Pago> pagos = pagoServicio.listarPagosPorCliente(clienteId);

            pagos.sort((p1, p2) -> p2.getFechaPago().compareTo(p1.getFechaPago()));

            model.addAttribute("pagos", pagos);


            return "cliente-facturas";

        } catch (Exception e) {
            return "redirect:/clientes/" + clienteId + "?error=NoSePudoCargarElHistorial";
        }
    }

    // metodo auxiliar para dar peso a los estados
    private int getPrioridadEstado(EstadoFactura estado) {
        switch (estado) {
            case VENCIDA: return 1;
            case VIGENTE: return 2;
            case PARCIAL: return 3;
            case PAGADA:  return 4;
            case ANULADA: return 5;
            default:      return 6;
        }
    }


}

