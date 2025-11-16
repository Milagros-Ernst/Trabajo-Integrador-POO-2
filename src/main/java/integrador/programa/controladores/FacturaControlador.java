package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import integrador.programa.servicios.FacturaServicio;
import jakarta.validation.Valid;
import integrador.programa.modelo.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/facturacion")
public class FacturaControlador {

    @Autowired
    private FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio){
        this.facturaServicio = facturaServicio;
    }

    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaServicio.listarFacturas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtener(@PathVariable String id) {
    return facturaServicio.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody Factura factura) {
        Factura saved = facturaServicio.agregarFactura(factura);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NotaCredito> eliminar(@PathVariable String id) {
        NotaCredito nota = facturaServicio.bajaFactura(id);
        if (nota == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(nota);
    }

    @PostMapping("/alta") 
    public ResponseEntity<?> emitirFactura(@RequestBody Map<String, Object> requestBody) {        
        try {
            Long idCliente = (long)requestBody.get("idCliente");
            
            Month periodo = Month.valueOf((String) requestBody.get("periodo"));
            LocalDate fechaVencimiento = LocalDate.parse((String) requestBody.get("fechaVencimiento"));
            
            @SuppressWarnings("unchecked") 
            Map<String, LocalDate> serviciosConFechaInicio = (Map<String, LocalDate>) requestBody.get("serviciosConFechaInicio"); 

            if (serviciosConFechaInicio == null || serviciosConFechaInicio.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe incluir al menos un servicio para facturar.");
            }
            
            Factura nuevaFactura = facturaServicio.emitirFacturaIndividual(
                idCliente, 
                periodo, 
                fechaVencimiento, 
                serviciosConFechaInicio
            );
            
            // si funciona
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno o formato de datos incorrecto: " + e.getMessage());
        }
    }

}
