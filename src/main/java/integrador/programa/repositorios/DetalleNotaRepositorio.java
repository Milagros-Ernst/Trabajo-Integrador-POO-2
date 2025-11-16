package integrador.programa.repositorios;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import integrador.programa.modelo.DetalleNota;

@Repository
public interface DetalleNotaRepositorio extends JpaRepository<DetalleNota, String> {
    
}
