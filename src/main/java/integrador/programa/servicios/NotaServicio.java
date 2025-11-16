package integrador.programa.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import integrador.programa.repositorios.NotaRepositorio;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.Factura;
import java.time.LocalDate;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotaServicio {
    @Autowired
    private final NotaRepositorio notaRepositorio;

    public NotaServicio(NotaRepositorio notaRepositorio) {
        this.notaRepositorio = notaRepositorio;
    }

    @Transactional
    public NotaCredito altaNotaPorFactura(Factura factura) {
        if (factura == null) return null;
        NotaCredito nota = new NotaCredito();
        nota.setNroSerie(factura.getNroSerie());
        nota.setPrecioTotal(factura.getPrecioTotal());
        nota.setTipo(factura.getTipo());
        nota.setFecha(LocalDate.now());
        nota.setFacturaAnulada(factura);
        return notaRepositorio.save(nota);
    }
}
