package integrador.programa.controladores;
import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.ServicioServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import integrador.programa.servicios.FacturaServicio;

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

    @Autowired
    private FacturaServicio facturaServicio;

    private Model model;


    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio";
    }


    // asignación de servicios a cliente



    // métodos para el historial de facturación de clientes

    @GetMapping("/clientes/{clienteId}/facturacion")
    public String irHistorialFacturacion(@PathVariable Long clienteId, Model model) {
        try {
            Cliente cliente = clienteService.buscarPorId(clienteId);
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
