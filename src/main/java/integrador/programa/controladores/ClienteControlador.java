package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.servicios.ClienteServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    // crear un nuevo cliente
    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteServicio.crearCliente(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // modificar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> modificar(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {
        try {
            // El servicio ahora maneja la validación y asignación del ID
            Cliente clienteModificado = clienteServicio.modificarCliente(id, cliente);
            return ResponseEntity.ok(clienteModificado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // baja lógica de cliente
    @PutMapping("/{id}/dar-de-baja")
    public ResponseEntity<Cliente> darDeBaja(@PathVariable Long id) {
        try {
            Cliente clienteDeBaja = clienteServicio.BajaCliente(id);
            return ResponseEntity.ok(clienteDeBaja);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // reactivar cliente
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<Cliente> reactivar(@PathVariable Long id) {
        try {
            Cliente clienteReactivado = clienteServicio.reactivarCliente(id);
            return ResponseEntity.ok(clienteReactivado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}