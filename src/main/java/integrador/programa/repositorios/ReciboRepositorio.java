package integrador.programa.repositorios;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Recibo;

public interface ReciboRepositorio extends JpaRepository<Recibo, String> {

    // Todos los recibos de un cliente (para el estado de cuenta)
    List<Recibo> findByCliente(Cliente cliente);

    // Buscar un recibo por su número correlativo
    Recibo findByNroRecibo(Long nroRecibo);
}
