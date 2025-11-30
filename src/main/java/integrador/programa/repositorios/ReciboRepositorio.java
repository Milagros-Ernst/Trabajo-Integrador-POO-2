package integrador.programa.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Recibo;

public interface ReciboRepositorio extends JpaRepository<Recibo, Long> {

    // Todos los recibos de un cliente (para el estado de cuenta)
    List<Recibo> findByCliente(Cliente cliente);

    // Buscar un recibo por su número correlativo
    Recibo findByNroRecibo(Long nroRecibo);

    // Obtener el último número de recibo generado
    @Query("SELECT COALESCE(MAX(r.nroRecibo), 0) FROM Recibo r")
    Long obtenerUltimoNumero();
}
