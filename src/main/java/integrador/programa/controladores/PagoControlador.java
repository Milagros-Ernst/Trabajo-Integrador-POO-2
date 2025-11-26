package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.servicios.PagoServicio;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/pagos")
public class PagoControlador {

    @Autowired
    private PagoServicio pagoServicio;

    public PagoControlador(PagoServicio pagoServicio) {
        this.pagoServicio = pagoServicio;
    }

    // Lista todos los pagos (a evaluar si es necesario)
    // @GetMapping
    // public ResponseEntity<List<Pago>> listar() {
    //     return ResponseEntity.ok(pagoServicio.listarPagos());
    // }

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

    // Registrar un pago total
    @PostMapping("/total")
    public ResponseEntity<?> registrarPagoTotal(@RequestBody Map<String, Object> requestBody) {
        try {
            Long idFactura = ((Number) requestBody.get("idFactura")).longValue();
            MetodoPago metodoPago = MetodoPago.valueOf((String) requestBody.get("metodoPago"));
            String empleadoResponsable = (String) requestBody.get("empleadoResponsable");
            String observaciones = (String) requestBody.getOrDefault("observaciones", "");

            Pago pago = pagoServicio.registrarPagoTotal(
                idFactura, 
                metodoPago, 
                empleadoResponsable, 
                observaciones
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar el pago: " + e.getMessage()));
        }
    }

    // pago parcial
    @PostMapping("/parcial")
    public ResponseEntity<?> registrarPagoParcial(@RequestBody Map<String, Object> requestBody) {
        try {
            Long idFactura = ((Number) requestBody.get("idFactura")).longValue();
            Double importe = ((Number) requestBody.get("importe")).doubleValue();
            MetodoPago metodoPago = MetodoPago.valueOf((String) requestBody.get("metodoPago"));
            String empleadoResponsable = (String) requestBody.get("empleadoResponsable");
            String observaciones = (String) requestBody.getOrDefault("observaciones", "");

            Pago pago = pagoServicio.registrarPagoParcial(
                idFactura, 
                importe, 
                metodoPago, 
                empleadoResponsable, 
                observaciones
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(pago);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar el pago: " + e.getMessage()));
        }
    }

    // calcular total pagado por factura
    @GetMapping("/factura/{idFactura}/total")
    public ResponseEntity<Map<String, Double>> calcularTotalPagado(@PathVariable Long idFactura) {
        Double total = pagoServicio.calcularTotalPagadoPorFactura(idFactura);
        return ResponseEntity.ok(Map.of("totalPagado", total));
    }
}
