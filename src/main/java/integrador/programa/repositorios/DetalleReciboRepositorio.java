package integrador.programa.repositorios;

import integrador.programa.modelo.DetalleRecibo;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Recibo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleReciboRepositorio extends JpaRepository<DetalleRecibo, Long> {

    // Todos los detalles de un determinado recibo
    List<DetalleRecibo> findByRecibo(Recibo recibo);

    // Todos los detalles que afectan a una determinada factura
    List<DetalleRecibo> findByFactura(Factura factura);
}
