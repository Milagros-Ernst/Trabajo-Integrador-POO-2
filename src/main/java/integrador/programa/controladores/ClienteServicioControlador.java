package integrador.programa.controladores;

import integrador.programa.modelo.ClienteServicio;
import integrador.programa.modelo.Servicio;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.ServicioServicio;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/clientes") 
public class ClienteServicioControlador {

    private final ClienteServicioServicio clienteServicioServicio;

    private final ServicioServicio servicioServicio;

    public ClienteServicioControlador(ClienteServicioServicio clienteServicioServicio,
                                      ServicioServicio servicioServicio) {
        this.clienteServicioServicio = clienteServicioServicio;
        this.servicioServicio = servicioServicio;
    }

    // Devuelve todos los servicios contratados por un cliente (ALTA y BAJA).
    @GetMapping("/{idCliente}/servicios-contratados")
    public ResponseEntity<?> listarServiciosContratados(@PathVariable Long idCliente) {
        try {
            // Llama al servicio para obtener la lista completa (altas + bajas).
            List<ClienteServicio> servicios = clienteServicioServicio.listarServiciosDeCliente(idCliente);
            return ResponseEntity.ok(servicios); // 200 OK con la lista en el body.
        } catch (IllegalArgumentException e) {
            Map<String, String> error = new HashMap<>(); // El servicio lanza una excepcion si el cliente no existe.
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error); // 404 NOT FOUND con un mensaje entendible para el front.
        }
    }

    // Devuelve solo los servicios ACTIVOS (en ALTA) de un cliente.
    @GetMapping("/{idCliente}/servicios-contratados/activos")
    public ResponseEntity<?> listarServiciosActivos(@PathVariable Long idCliente) {
        try {
            // Llama al servicio para obtener solo los registros con estado ALTA.
            List<ClienteServicio> activos = clienteServicioServicio.listarServiciosActivosDeCliente(idCliente);

            return ResponseEntity.ok(activos);

        } catch (IllegalArgumentException e) {
            // Caso en el que el cliente no existe.
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    //  Devuelve todos los servicios definidos en el sistema
    @GetMapping("/servicios-del-sistema")
    public ResponseEntity<List<Servicio>> listarServiciosDelSistema() {
        List<Servicio> servicios = servicioServicio.listarTodos(); // Obtiene todos los servicios desde ServicioServicio.
        return ResponseEntity.ok(servicios); // 200 OK con el catálogo de servicios.
    }

    // Asigna un servicio a un cliente.
    @PostMapping("/{idCliente}/servicios/{idServicio}")
    public ResponseEntity<?> asignarServicio(
            @PathVariable Long idCliente,   // id del cliente tomado de la URL
            @PathVariable String idServicio // id del servicio tomado de la URL
    ) {

        try {
            // Llama al servicio de negocio para aplicar la lógica de ALTA/reactivación.
            ClienteServicio nuevo = clienteServicioServicio.asignarServicioACliente(idCliente, idServicio);

            // Devuelve 201 CREATED porque se creó o reactivó una relación.
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);

        } catch (IllegalArgumentException e) {
            // Se lanza cuando: el cliente no existe o el servicio no existe
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);  // En ambos casos respondemos con 404 NOT FOUND.

        } catch (IllegalStateException e) {
            // Se lanza cuando la regla de negocio impide la operación
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            // 400 BAD REQUEST porque la petición es inválida según las reglas.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    // Da de BAJA lógica un servicio que ya estaba contratado por el cliente.
    @PutMapping("/servicios-cliente/{idClienteServicio}/baja")
    public ResponseEntity<?> darDeBaja(@PathVariable String idClienteServicio) {

        try {
            // Llama al servicio de negocio para hacer la BAJA lógica
            ClienteServicio baja = clienteServicioServicio.darDeBajaServicioCliente(idClienteServicio);
            return ResponseEntity.ok(baja); // Devuelve 200 OK con el objeto actualizado

        } catch (IllegalArgumentException e) {
            // Se lanza cuando la relación Cliente–Servicio no existe.
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            // 404 NOT FOUND porque no se encontró el registro a dar de baja.
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);

        } catch (IllegalStateException e) {
            // Se lanza cuando el servicio ya estaba dado de baja.
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());

            // 400 BAD REQUEST porque la operación no tiene sentido (ya estaba en BAJA).
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}
