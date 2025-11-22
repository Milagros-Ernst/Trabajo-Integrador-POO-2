package integrador.programa.repositorios;

import integrador.programa.modelo.NotaCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaRepositorio extends JpaRepository<NotaCredito, Long> {

}
