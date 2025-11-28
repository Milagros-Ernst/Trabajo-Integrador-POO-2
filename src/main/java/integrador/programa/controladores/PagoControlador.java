package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.FacturaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.servicios.PagoServicio;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/administrar-pagos")
public class PagoControlador {

    private PagoServicio pagoServicio;
    private final ClienteService clienteService;
    private final FacturaServicio facturaServicio;

    @Autowired
    public PagoControlador(PagoServicio pagoServicio,
                           ClienteService clienteService,
                           FacturaServicio facturaServicio) {
        this.pagoServicio = pagoServicio;
        this.clienteService = clienteService;
        this.facturaServicio = facturaServicio;
    }

    @GetMapping
    public String irAAdministrarPagos(@RequestParam(required = false) Long clienteId, Model model) {

        model.addAttribute("clientes", clienteService.listarClientesActivos());

        if (clienteId != null) {
            try {
                Cliente cliente = clienteService.buscarPorId(clienteId);
                model.addAttribute("clienteSeleccionado", cliente);

                List<Factura> todasLasFacturas = facturaServicio.buscarFacturasPorCliente(cliente);
                List<Factura> facturasPendientes = todasLasFacturas.stream()
                        .filter(f -> f.getEstado() == EstadoFactura.VIGENTE ||
                                f.getEstado() == EstadoFactura.PARCIAL ||
                                f.getEstado() == EstadoFactura.VENCIDA)
                        .collect(Collectors.toList());

                model.addAttribute("facturasPendientes", facturasPendientes);
            } catch (Exception e) {
                model.addAttribute("error", "Error al cargar datos del cliente: " + e.getMessage());
            }
        }
        return "administrar-pagos";
    }

    //  Procesar el Pago
    @PostMapping("/preparar")
    public String prepararPago(@RequestParam Long clienteId,
                               @RequestParam(required = false) List<Long> facturasIds,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (facturasIds == null || facturasIds.isEmpty()) {
            redirectAttributes.addAttribute("clienteId", clienteId);
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos una factura.");
            return "redirect:/administrar-pagos";
        }

        try {
            Cliente cliente = clienteService.buscarPorId(clienteId);

            // calcular el total de las facturas seleccionadas
            double totalCalculado = 0.0;
            for (Long id : facturasIds) {
                Factura f = facturaServicio.buscarPorId(id).orElseThrow();
                totalCalculado += f.calcularSaldoPendiente();
            }

            model.addAttribute("cliente", cliente);
            model.addAttribute("facturasIds", facturasIds);
            model.addAttribute("cantidadFacturas", facturasIds.size());
            model.addAttribute("totalCalculado", totalCalculado);

            return "procesar-pago";

        } catch (Exception e) {
            redirectAttributes.addAttribute("clienteId", clienteId);
            redirectAttributes.addFlashAttribute("error", "Error al preparar el pago: " + e.getMessage());
            return "redirect:/pagos/administrar";
        }
    }

    @PostMapping("/finalizar")
    public String finalizarPago(@RequestParam Long clienteId,
                                @RequestParam List<Long> facturasIds,
                                @RequestParam Double montoPagar,
                                @RequestParam MetodoPago metodoPago,
                                @RequestParam(required = false) String observaciones,
                                RedirectAttributes redirectAttributes) {

        //hardcodeamos el administrador:
        String empleadoResponsable = "Administrador";

        try {
            pagoServicio.registrarPagoMasivo(
                    facturasIds,
                    montoPagar,
                    metodoPago,
                    empleadoResponsable,
                    observaciones
            );

            redirectAttributes.addAttribute("clienteId", clienteId);
            redirectAttributes.addFlashAttribute("success", "Pago registrado correctamente por $" + montoPagar);
        } catch (Exception e) {
            redirectAttributes.addAttribute("clienteId", clienteId);
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
        }

        return "redirect:/administrar-pagos";
    }

    // dejo estos métodos por las dudas, pero habría que revisar si son necesarios..

    // obtener un pago por ID
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtener(@PathVariable Long id) {
        return pagoServicio.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Listar pagos por factura
    @GetMapping("/factura/{idFactura}")
    public ResponseEntity<List<Pago>> listarPorFactura(@PathVariable Long idFactura) {
        return ResponseEntity.ok(pagoServicio.listarPagosPorFactura(idFactura));
    }

    // Listar pagos por cliente
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<Pago>> listarPorCliente(@PathVariable Long idCliente) {
        return ResponseEntity.ok(pagoServicio.listarPagosPorCliente(idCliente));
    }

    // calcular total pagado por factura
    @GetMapping("/factura/{idFactura}/total")
    public ResponseEntity<Map<String, Double>> calcularTotalPagado(@PathVariable Long idFactura) {
        Double total = pagoServicio.calcularTotalPagadoPorFactura(idFactura);
        return ResponseEntity.ok(Map.of("totalPagado", total));
    }
}
