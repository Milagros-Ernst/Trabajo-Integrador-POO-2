package integrador.programa.repositorios;

import integrador.programa.modelo.LogFacturacionMasiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogFacturacionMasRepositorio extends JpaRepository<LogFacturacionMasiva, String>{
    
}
