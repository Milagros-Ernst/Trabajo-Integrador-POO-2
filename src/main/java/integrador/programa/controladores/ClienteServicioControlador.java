package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.ServicioServicio;
import integrador.programa.servicios.ClienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping
public class ClienteServicioControlador {

    private final ClienteServicioServicio clienteServicioServicio;
    private final ServicioServicio servicioServicio;
    private final ClienteService clienteService;

    public ClienteServicioControlador(ClienteServicioServicio clienteServicioServicio,
                                      ServicioServicio servicioServicio,
                                      ClienteService clienteService) {
        this.clienteServicioServicio = clienteServicioServicio;
        this.servicioServicio = servicioServicio;
        this.clienteService = clienteService; //
    }

    @GetMapping("/clientes/{id}/asignar")
    public String irAAsignarServicios(@PathVariable Long id, Model model) {
        try {
            Cliente cliente = clienteService.buscarPorId(id);
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

    // baja de servicio a cliente
    @PostMapping("/clientes/{clienteId}/asignaciones/{asigId}/eliminar")
    public String eliminarServicioDeCliente(@PathVariable Long clienteId,
                                            @PathVariable String asigId) {
        try {
            clienteServicioServicio.darDeBajaServicioCliente(asigId);

            return "redirect:/clientes/" + clienteId + "/asignar";

        } catch (Exception e) {
            System.err.println("Error al eliminar servicio: " + e.getMessage());
            return "redirect:/clientes/" + clienteId + "/asignar?error=NoSePudoEliminar";
        }
    }










}
