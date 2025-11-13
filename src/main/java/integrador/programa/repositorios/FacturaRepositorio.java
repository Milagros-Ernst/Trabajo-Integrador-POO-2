package integrador.programa.repositorios;

import integrador.programa.modelo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface FacturaRepositorio extends JpaRepository<Factura, String> {
    

    //List<Factura> buscarPorEstado(EstadoFactura estado);

    //List<Factura> buscarPorTipo(TipoComprobante tipo);
    
    //Optional<Factura> buscarPorNroSerie(String nroSerie);
}
