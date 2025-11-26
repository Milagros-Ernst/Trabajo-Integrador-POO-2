package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.LogFacturacionMasiva;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.ServicioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import integrador.programa.servicios.FacturaServicio;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    private Model model;


    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio";
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

    // asignación de servicios a cliente

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



    // métodos para el historial de facturación de clientes

    @GetMapping("/clientes/{clienteId}/facturacion")
    public String irHistorialFacturacion(@PathVariable Long clienteId, Model model) {
        try {
            Cliente cliente = clienteServicio.buscarPorId(clienteId);
            model.addAttribute("cliente", cliente);

            List<Factura> facturas = facturaServicio.listarFacturas();

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
            return "cliente-facturas"; // Nombre de tu nueva vista HTML

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
