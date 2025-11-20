package integrador.programa.repositorios;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepositorio extends JpaRepository<Servicio, String> {

    // NUEVO: listar servicios según su estado (ALTA o BAJA)
    List<Servicio> findByEstadoServicio(EstadoServicio estadoServicio);
}
