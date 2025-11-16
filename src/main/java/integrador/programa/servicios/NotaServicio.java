package integrador.programa.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import integrador.programa.repositorios.DetalleNotaRepositorio;
import integrador.programa.repositorios.NotaRepositorio;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.DetalleFactura;
import integrador.programa.modelo.DetalleNota;
import integrador.programa.modelo.Factura;
import java.time.LocalDate;

@Service
public class NotaServicio {
    private static final String RESPONSABLE = "Admin Hardcodeado";

    @Autowired
    private final NotaRepositorio notaRepositorio;


    public NotaServicio(NotaRepositorio notaRepositorio) {
        this.notaRepositorio = notaRepositorio;
    }

    public NotaCredito altaNotaPorFactura(Factura facturaAnulada, String motivoAnulacion) {
        NotaCredito nota = new NotaCredito();
        nota.setFecha(LocalDate.now());
        nota.setEmpleadoResponsable(RESPONSABLE);
        nota.setMotivoAnulacion(motivoAnulacion); 
        
        nota.setPrecioTotal(facturaAnulada.getPrecioTotal() * -1); 
        nota.setFacturaAnulada(facturaAnulada); 
        
        for (DetalleFactura detFactura : facturaAnulada.getDetalles()) {
            DetalleNota detNota = new DetalleNota();
            detNota.setDescripcion(detFactura.getDescripcion());
            detNota.setPrecio(detFactura.getPrecio() * -1); 
            detNota.setNotaCredito(nota);
            
            nota.getDetallesNota().add(detNota); 
        }
        
        return notaRepositorio.save(nota);
    }
}
