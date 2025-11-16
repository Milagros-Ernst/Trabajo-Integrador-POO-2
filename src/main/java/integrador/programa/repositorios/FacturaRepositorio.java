package integrador.programa.repositorios;

import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.TipoComprobante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, String> {
    List<Factura> findByEstado(EstadoFactura estado);
    List<Factura> findByTipo(TipoComprobante tipo);
    Optional<Factura> findByNroSerie(String nroSerie);
}
