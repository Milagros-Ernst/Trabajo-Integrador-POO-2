package integrador.programa.repositorios;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.ClienteServicio;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;

public interface ClienteServicioRepositorio extends JpaRepository<ClienteServicio, String> {

    // Obtiene todos los registros (ALTA y BAJA) que tenga un cliente.
    List<ClienteServicio> findByCliente(Cliente cliente);

    // Obtiene los servicios de un cliente filtrando por un estado concreto
    List<ClienteServicio> findByClienteAndEstadoServicio(Cliente cliente, EstadoServicio estadoServicio);

    // Buscan una relacion entre un cliente y un servicio.
    Optional<ClienteServicio> findByClienteAndServicio(Cliente cliente, Servicio servicio);
}
