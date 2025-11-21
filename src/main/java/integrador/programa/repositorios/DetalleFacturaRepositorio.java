package integrador.programa.repositorios;

import integrador.programa.modelo.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DetalleFacturaRepositorio extends JpaRepository<DetalleFactura, Long> {
    
    // dato de color, spring boot pide que se use "findBy" al inicio del método o sino explota porque le pinta esa
    List<DetalleFactura> findByFacturaIdFactura(Long idFactura);
}
