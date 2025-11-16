package integrador.programa.repositorios;

import integrador.programa.modelo.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositorio JPA para la entidad Servicio.
 *
 * Extiende JpaRepository para tener:
 *  - save()      → guardar/actualizar servicios
 *  - findAll()   → listar todos los servicios
 *  - findById()  → buscar por ID
 *  - deleteById()→ eliminar por ID
 */
@Repository
public interface ServicioRepositorio extends JpaRepository<Servicio, String> {

    // Por ahora no necesitamos métodos extra para la HU 04 (Alta de Servicio).
}
