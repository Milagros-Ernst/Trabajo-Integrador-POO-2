package integrador.programa.repositorios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface ClienteRepositorio extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
    
    boolean existsByTipoDocumentoAndNumeroDocumento(TipoDocumento tipoDocumento, String numeroDocumento);
    
    List<Cliente> findByApellidoContainingIgnoreCase(String apellido);
    List<Cliente> findByNombreContainingIgnoreCase(String nombre);
    
    Optional<Cliente> findByMail(String mail);
}