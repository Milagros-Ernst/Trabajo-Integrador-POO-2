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

    // el valor de las notas lo vamos a manejar en positivo como en las notas reales

    public NotaCredito altaNotaPorFactura(Factura facturaAnulada, String motivoAnulacion) {
        NotaCredito nota = new NotaCredito();
        nota.setFecha(LocalDate.now());
        nota.setEmpleadoResponsable(RESPONSABLE);
        nota.setMotivoAnulacion(motivoAnulacion); 
        nota.setTipo(facturaAnulada.getTipo());
        
        nota.setPrecioTotal(facturaAnulada.getPrecioTotal()); 
        nota.setFacturaAnulada(facturaAnulada); 
        
        for (DetalleFactura detFactura : facturaAnulada.getDetalles()) {
            DetalleNota detNota = new DetalleNota();
            detNota.setDescripcion(detFactura.getDescripcion());
            detNota.setPrecio(detFactura.getPrecio()); 
            detNota.setNotaCredito(nota);

            detNota.setDetalleFactura(detFactura);
            nota.getDetallesNota().add(detNota); 
        }
        
        return notaRepositorio.save(nota);
    }
}
