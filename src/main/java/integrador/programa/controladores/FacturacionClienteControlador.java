package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.DetalleFactura;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Pago;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.PagoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class FacturacionClienteControlador {

    private final ClienteService clienteService;
    private final ClienteServicioServicio clienteServicioServicio;
    private final FacturaServicio facturaServicio;
    private final PagoServicio pagoServicio;


    @Autowired
    public FacturacionClienteControlador(ClienteService clienteService,
                                         ClienteServicioServicio clienteServicioServicio,
                                         FacturaServicio facturaServicio,
                                         PagoServicio pagoServicio) {
        this.clienteService = clienteService;
        this.clienteServicioServicio = clienteServicioServicio;
        this.facturaServicio = facturaServicio;
        this.pagoServicio = pagoServicio;
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

    @GetMapping("/facturacion/ver/{id}")
    public String verDetalleFactura(@PathVariable Long id, Model model) {
        return facturaServicio.buscarPorId(id).map(factura -> {
            model.addAttribute("factura", factura);
            model.addAttribute("cliente", factura.getCliente());

            double subtotalNeto = factura.getDetalles().stream()
                    .mapToDouble(d -> d.getPrecio())
                    .sum();
            model.addAttribute("subtotalNeto", subtotalNeto);

            // para separar el iva dependiendo de cada tipo, mapeamos
            Map<String, Double> ivaDesglosado = new HashMap<>();

            // para cada alicuota de cada detalle, vamos sumando (todas las de 21%, etc)
            for (DetalleFactura detalle : factura.getDetalles()) {
                String alicuota = detalle.getAlicuotaIva();
                double montoIva = detalle.getMontoIvaCalculado();

                ivaDesglosado.put(alicuota, ivaDesglosado.getOrDefault(alicuota, 0.0) + montoIva);
            }

            // se pasa el mapa a la vista
            model.addAttribute("ivaDesglosado", ivaDesglosado);
            double totalIva = factura.getDetalles().stream()
                    .mapToDouble(d -> d.getMontoIvaCalculado())
                    .sum();


            return "factura-detalle";
        }).orElse("redirect:/cliente-facturas");
    }


}