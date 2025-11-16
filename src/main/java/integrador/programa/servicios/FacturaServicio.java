package integrador.programa.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import integrador.programa.modelo.Factura;
import integrador.programa.repositorios.FacturaRepositorio;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaServicio {

    @Autowired
    private final FacturaRepositorio facturaRepositorio;
    private final NotaServicio notaServicio;

    public FacturaServicio(FacturaRepositorio facturaRepositorio, NotaServicio notaServicio) {
        this.facturaRepositorio = facturaRepositorio;
        this.notaServicio = notaServicio;
    }
     
    public Factura agregarFactura(Factura factura) {
        return facturaRepositorio.save(factura);
    }

    public List<Factura> listarFacturas() {
        return facturaRepositorio.findAll();
    }

    public Optional<Factura> buscarPorId(String id) {
        return facturaRepositorio.findById(id);
    }

    @Transactional
    public NotaCredito bajaFactura(String id) {
        Optional<Factura> opt = facturaRepositorio.findById(id);
        if (opt.isEmpty()) return null;
        Factura factura = opt.get();
        // marco factura como anulada y guardo
        factura.setEstado(EstadoFactura.ANULADA);
        facturaRepositorio.save(factura);
        NotaCredito nota = notaServicio.altaNotaPorFactura(factura);
        return nota;
    }
}
