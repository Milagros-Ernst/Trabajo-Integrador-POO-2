package integrador.programa.repositorios;

import integrador.programa.modelo.NotaCredito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotaCreditoRepository extends JpaRepository<NotaCredito, Integer> {
    // Spring Data JPA provee automáticamente findById(Integer id), save(NotaCredito nc), etc.
    // El 'Integer' coincide con el tipo de dato de tu idNotaCredito.
}