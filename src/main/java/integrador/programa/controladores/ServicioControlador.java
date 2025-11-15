package integrador.programa.controladores;

import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ServicioServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("servicios")
public class ServicioControlador {

    private final ServicioServicio servicioServicio;

    public ServicioControlador(ServicioServicio servicioServicio) {
        this.servicioServicio = servicioServicio;
    }

    // obtener todos los servicios
    @GetMapping
    public ResponseEntity<List<Servicio>> listarTodos() {
        List<Servicio> servicios = servicioServicio.listarTodos();
        return ResponseEntity.ok(servicios);
    }

    // obtener un servicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtenerPorId(@PathVariable String id) {
        try {
            Servicio servicio = servicioServicio.buscarPorId(id);
            return ResponseEntity.ok(servicio);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // crear un nuevo servicio  (HU 04 - Alta de Servicio)
    @PostMapping
    public ResponseEntity<Servicio> crear(@Valid @RequestBody Servicio servicio) {
        try {
            Servicio nuevoServicio = servicioServicio.agregarServicio(servicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // actualizar servicio por ID
    @PutMapping("/{id}")
    public ResponseEntity<Servicio> actualizar(
            @PathVariable String id,
            @Valid @RequestBody Servicio servicioActualizado) {
        try {
            Servicio actualizado = servicioServicio.actualizarServicio(id, servicioActualizado);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // eliminar servicio
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable String id) {
        try {
            servicioServicio.eliminarServicio(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
