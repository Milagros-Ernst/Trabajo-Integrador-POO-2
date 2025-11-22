package integrador.programa.repositorios;

import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.TipoComprobante;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, Long> {
    List<Factura> findByEstado(EstadoFactura estado);
    List<Factura> findByTipo(TipoComprobante tipo);
    Optional<Factura> findByNroSerie(Long nroSerie);

    @Query(value = "SELECT f.nroSerie FROM Factura f WHERE f.tipo = :tipo ORDER BY f.nroSerie DESC LIMIT 1")
    String findMaxNroSerieByTipo(@Param("tipo") TipoComprobante tipo);
}
