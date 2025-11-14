package integrador.programa.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import integrador.programa.modelo.Factura;
import integrador.programa.repositorios.FacturaRepositorio;

@Service
public class FacturaServicio {

    @Autowired
    private final FacturaRepositorio facturaRepositorio;

    public FacturaServicio(FacturaRepositorio facturaRepositorio) {
        this.facturaRepositorio = facturaRepositorio;
    }
     
    public void agregarFactura(Factura factura) {
        facturaRepositorio.save(factura);
    }
}
