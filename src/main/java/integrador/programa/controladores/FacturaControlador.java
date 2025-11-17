package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import integrador.programa.servicios.ClienteServicio;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.ServicioServicio;
import jakarta.validation.Valid;
import integrador.programa.modelo.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/facturacion")
public class FacturaControlador {

    @Autowired
    private FacturaServicio facturaServicio;
    @Autowired 
    private ClienteServicio clienteServicio; 
    @Autowired 
    private ServicioServicio servicioServicio;

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

    @PostMapping("/alta") 
    public ResponseEntity<?> emitirFactura(@RequestBody Map<String, Object> requestBody) {         
        try {
            // 1. Obtenemos los datos del JSON
            // Usamos ((Number) ...).longValue() para evitar errores de casteo
            Long idCliente = ((Number)requestBody.get("idCliente")).longValue(); 
            
            int periodo = Integer.valueOf((String) requestBody.get("periodo"));
            LocalDate fechaVencimiento = LocalDate.parse((String) requestBody.get("fechaVencimiento"));
            
            @SuppressWarnings("unchecked") 
            Map<String, LocalDate> serviciosConFechaInicio = (Map<String, LocalDate>) requestBody.get("serviciosConFechaInicio"); 

            if (serviciosConFechaInicio == null || serviciosConFechaInicio.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe incluir al menos un servicio para facturar.");
            }
            
            // --- INICIO DE LA CORRECCIÓN ---

            // 2. Buscamos el Cliente (Arregla el Arg 1: Long -> Cliente)
            Cliente clienteAFacturar = clienteServicio.buscarPorId(idCliente);
            
            // 3. Extraemos los IDs de los servicios (Arregla el Arg 4 -> Arg 2)
            // Las llaves (keys) de tu Map son los IDs de los servicios
            List<String> serviciosIds = new ArrayList<>(serviciosConFechaInicio.keySet());

            // 4. Llamamos al servicio con los tipos y el orden CORRECTO
            Factura nuevaFactura = facturaServicio.emitirFacturaIndividual(
                clienteAFacturar,   // Argumento 1: Cliente
                serviciosIds,       // Argumento 2: List<String>
                periodo,            // Argumento 3: int
                fechaVencimiento    // Argumento 4: LocalDate
            );
            
            // --- FIN DE LA CORRECCIÓN ---
            
            // si funciona
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);

        } catch (NoSuchElementException e) {
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cliente no encontrado con ID: " + requestBody.get("idCliente"));
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno o formato de datos incorrecto: " + e.getMessage());
        }
    }

    @PostMapping("/alta/masiva") 
    public ResponseEntity<?> emitirFacturaMasiva(@RequestBody Map<String, Object> requestBody) {
        try {
            @SuppressWarnings("unchecked") 
            List<String> idServiciosFacturar = (List<String>) requestBody.get("idServicios");
            int periodo = Integer.valueOf((String) requestBody.get("periodo"));
            LocalDate fechaVencimiento = LocalDate.parse((String) requestBody.get("fechaVencimiento"));

            if (idServiciosFacturar == null || idServiciosFacturar.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe seleccionar al menos un servicio.");
            }
            
            LogFacturacionMasiva registro = facturaServicio.emitirFacturaMasiva(
                idServiciosFacturar, 
                periodo, 
                fechaVencimiento
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(registro);

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno o formato de datos incorrecto: " + e.getMessage());
        }
    }

    @PostMapping("/anular/{id}") 
    public ResponseEntity<?> anularFactura(@PathVariable String id, 
                                            @RequestBody Map<String, String> requestBody) {
    
    try {
        String motivo = requestBody.get("motivoAnulacion"); 
        
        if (motivo == null || motivo.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("El campo 'motivoAnulacion' es obligatorio.");
        }
        
        NotaCredito nota = facturaServicio.bajaFactura(id, motivo);
        
        return ResponseEntity.ok(nota);
    } catch (IllegalArgumentException | IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno al anular la factura.");
    }
}

}
