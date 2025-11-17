package integrador.programa.servicios;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.ClienteServicio;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.repositorios.ClienteRepositorio;
import integrador.programa.repositorios.ClienteServicioRepositorio;
import integrador.programa.repositorios.ServicioRepositorio;

@Service
public class ClienteServicioServicio {

    private final ClienteServicioRepositorio clienteServicioRepositorio;

    // Repositorio de clientes (para buscar por id)
    private final ClienteRepositorio clienteRepositorio;

    // Repositorio de servicios (para buscar por id)
    private final ServicioRepositorio servicioRepositorio;

    public ClienteServicioServicio(ClienteServicioRepositorio clienteServicioRepositorio,
                                   ClienteRepositorio clienteRepositorio,
                                   ServicioRepositorio servicioRepositorio) {
        this.clienteServicioRepositorio = clienteServicioRepositorio;
        this.clienteRepositorio = clienteRepositorio;
        this.servicioRepositorio = servicioRepositorio;
    }

    // Asigna un servicio a un cliente.
    @Transactional
    public ClienteServicio asignarServicioACliente(Long idCliente, String idServicio) {
        // Buscar el cliente por su ID; si no existe, se lanza excepción
        Cliente cliente = clienteRepositorio.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente con ID: " + idCliente));

        // Buscar el servicio por su ID; si no existe, se lanza excepción
        Servicio servicio = servicioRepositorio.findById(idServicio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el servicio con ID: " + idServicio));

        // Verificar si ya existe una relación entre ese cliente y ese servicio
        return clienteServicioRepositorio.findByClienteAndServicio(cliente, servicio)
                .map(csExistente -> {
                    // Si ya está activo, no permitimos duplicar
                    if (csExistente.estaActivo()) {
                        throw new IllegalStateException(
                                "El cliente ya tiene dado de alta este servicio.");
                    }

                    // Si existe pero está dado de baja → lo reactivamos 
                    csExistente.darDeAlta(LocalDate.now());
                    return clienteServicioRepositorio.save(csExistente);
                })
                .orElseGet(() -> {
                    // Si no existe la relación → creamos un vínculo nuevo en ALTA
                    ClienteServicio nuevo = new ClienteServicio();
                    nuevo.setCliente(cliente);
                    nuevo.setServicio(servicio);
                    nuevo.darDeAlta(LocalDate.now()); 
                    return clienteServicioRepositorio.save(nuevo);
                });
    }

    // Da de baja lógica un servicio para un cliente a partir del ID de ClienteServicio.
    @Transactional
    public ClienteServicio darDeBajaServicioCliente(String idClienteServicio) {
        // Buscar la relación Cliente–Servicio por su ID técnico (UUID)
        ClienteServicio cs = clienteServicioRepositorio.findById(idClienteServicio)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró la relación Cliente–Servicio con ID: " + idClienteServicio));

        // Si ya estaba dado de baja, avisamos
        if (cs.getEstadoServicio() == EstadoServicio.BAJA && cs.getFechaBaja() != null) {
            throw new IllegalStateException(
                    "El servicio ya estaba dado de baja para este cliente.");
        }

        // Aplicamos baja lógica
        cs.darDeBaja(LocalDate.now());
        return clienteServicioRepositorio.save(cs);
    }

    // Lista todos los servicios (ALTAS y BAJAS) asociados a un cliente.
    @Transactional(readOnly = true)
    public List<ClienteServicio> listarServiciosDeCliente(Long idCliente) {
        // Primero obtenemos el cliente para asegurar que existe
        Cliente cliente = clienteRepositorio.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente con ID: " + idCliente));

        // Buscamos todas las relaciones de ese cliente
        return clienteServicioRepositorio.findByCliente(cliente);
    }

    // Lista solo los servicios activos (ALTA) que tiene un cliente.
    @Transactional(readOnly = true)
    public List<ClienteServicio> listarServiciosActivosDeCliente(Long idCliente) {
        // Primero obtenemos el cliente para asegurar que existe
        Cliente cliente = clienteRepositorio.findById(idCliente)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se encontró el cliente con ID: " + idCliente));

        // Buscamos únicamente los vínculos en estado ALTA
        return clienteServicioRepositorio.findByClienteAndEstadoServicio(
                cliente, EstadoServicio.ALTA);
    }
}
