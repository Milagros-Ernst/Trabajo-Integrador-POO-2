package integrador.programa.repositorios;

import integrador.programa.modelo.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaRepository extends JpaRepository<Cuenta, Integer> {
    // Asumo que la PK de tu entidad 'Cuenta' es un Integer.
    // Si es otro tipo (ej. String, Long), cámbialo aquí.
}