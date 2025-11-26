package integrador.programa.controladores;

import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ServicioServicio;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller                   
@RequestMapping("/api/servicios")
public class ServicioControlador {

    private final ServicioServicio servicioServicio;

    public ServicioControlador(ServicioServicio servicioServicio) {
        this.servicioServicio = servicioServicio;
    }
    
    // Listar todos los servicios con estado ALTA
    @GetMapping
    public ResponseEntity<List<Servicio>> listarTodos() {
        List<Servicio> servicios = servicioServicio.listarTodos(); // Llama al servicio para obtener todos los registros
        return ResponseEntity.ok(servicios);  // Devuelve 200 OK con la lista de servicios en el body
    }

    // Obtener un servicio por ID
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable String id) {

        try {
            Servicio servicio = servicioServicio.buscarPorId(id);   // Intenta buscar el servicio en la base de datos
            return ResponseEntity.ok(servicio); // Si se encuentra, se devuelve con código 200 OK

        } catch (IllegalArgumentException e) {
            // Si no existe, se devuelve un mensaje claro al usuario
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // Código 404 Not Found con mensaje
        }
    }

    //  Crear un nuevo servicio (Alta)
    @PostMapping
    public ResponseEntity<?> crear( @Valid @RequestBody Servicio servicio, BindingResult bindingResult) {  // Captura los errores de validación y @Valid activa la validación automática

        // Verifica si hubo errores de validación
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();  // Mapa para guardar campo → mensaje de error

            // Recorre todos los errores de validación producidos por @Valid
            for (FieldError error : bindingResult.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errores);   // Devuelve 400 Bad Request con mensajes claros
        }
        Servicio nuevoServicio = servicioServicio.agregarServicio(servicio);    // Si los datos son válidos, se crea el servicio
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio); // Devuelve 201 Created con el objeto creado
    }

    // Actualizar un servicio existente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(
            @PathVariable String id,                  // Recibe el ID dentro de la URL
            @Valid @RequestBody Servicio servicioActualizado, // Valida el JSON entrante
            BindingResult bindingResult) {            // Captura errores

        // Primero validar los campos enviados
        if (bindingResult.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errores.put(error.getField(), error.getDefaultMessage());
            }           
            return ResponseEntity.badRequest().body(errores);  // Devuelve todos los mensajes de error por campo
        }

        try {
            Servicio servicio = servicioServicio.actualizarServicio(id, servicioActualizado); // Ejecuta actualización por ID
            return ResponseEntity.ok(servicio); // Devuelve el objeto actualizado con código 200
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();    // Si el ID no está registrado, se informa con un mensaje
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 No se encontro
        }
    }

    // Eliminar un servicio por ID
    @DeleteMapping("/{id}")
    public ResponseEntity<?> desactivar(@PathVariable String id) {
        try {
            servicioServicio.eliminarServicio(id);
            return ResponseEntity.noContent().build();  // 204 sin contenido

        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
}
