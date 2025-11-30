package integrador.programa.repositorios;

import integrador.programa.modelo.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Long> {
    // Por ahora no hay métodos extra.
    // Las búsquedas por factura/cliente se resuelven vía Recibo/DetalleRecibo.
}
