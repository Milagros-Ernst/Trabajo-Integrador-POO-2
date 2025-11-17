package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import integrador.programa.servicios.ClienteServicio;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clientes")
public class ClienteControlador {

    private final ClienteServicio clienteServicio;

    public ClienteControlador(ClienteServicio clienteServicio) {
        this.clienteServicio = clienteServicio;
    }

    // obtener todos los clientes
    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        List<Cliente> clientes = clienteServicio.listarTodos();
        return ResponseEntity.ok(clientes);
    }

    // obtener un cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        try {
            Cliente cliente = clienteServicio.buscarPorId(id);
            return ResponseEntity.ok(cliente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // buscar cliente por tipo y numero de documento
    @GetMapping("/buscar")
    public ResponseEntity<Cliente> buscarPorDocumento(
            @RequestParam TipoDocumento tipoDocumento,
            @RequestParam String numeroDocumento) {
        Optional<Cliente> cliente = clienteServicio.buscarPorDocumento(tipoDocumento, numeroDocumento);
        return cliente.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    // actualizar cliente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {
        try {
            // El servicio ahora maneja la validación y asignación del ID
            Cliente clienteActualizado = clienteServicio.actualizarCliente(id, cliente);
            return ResponseEntity.ok(clienteActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // eliminar cliente
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> eliminar(@PathVariable Long id) {
    //     try {
    //         clienteServicio.eliminarCliente(id);
    //         return ResponseEntity.noContent().build();
    //     } catch (Exception e) {
    //         return ResponseEntity.notFound().build();
    //     }
    // }
}