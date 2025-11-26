package integrador.programa.repositorios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepositorio extends JpaRepository<Pago, Long> {
    
    // Buscar pagos por factura
    List<Pago> findByFactura(Factura factura);
    
    List<Pago> findByFacturaIdFactura(Long idFactura);
    
    // Buscar pagos por cliente (a través de la factura)
    @Query("SELECT p FROM Pago p WHERE p.factura.cliente.idCuenta = :idCliente")
    List<Pago> findByClienteId(@Param("idCliente") Long idCliente);
    
    // Calcular total pagado de una factura
    @Query("SELECT COALESCE(SUM(p.importe), 0.0) FROM Pago p WHERE p.factura.idFactura = :idFactura")
    Double calcularTotalPagadoPorFactura(@Param("idFactura") Long idFactura);
}
