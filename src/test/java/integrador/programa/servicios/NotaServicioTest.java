package integrador.programa.servicios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import integrador.programa.modelo.DetalleFactura;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.enumeradores.TipoComprobante;
import integrador.programa.repositorios.NotaRepositorio;


@ExtendWith(MockitoExtension.class)
public class NotaServicioTest {

    @InjectMocks
    NotaServicio notaServicio;

    @Mock
    NotaRepositorio notaRepositorio;

    private Factura facturaParaAnular;
    private DetalleFactura detalleFactura1;
    private DetalleFactura detalleFactura2;

    @BeforeEach
    public void iniciar() {
        detalleFactura1 = new DetalleFactura();
        detalleFactura1.setDescripcion("Servicio de Internet");
        detalleFactura1.setPrecio(1500);
        
        detalleFactura2 = new DetalleFactura();
        detalleFactura2.setDescripcion("Instalación cableada");
        detalleFactura2.setPrecio(500);

        facturaParaAnular = new Factura();
        ReflectionTestUtils.setField(facturaParaAnular, "idFactura", 10L);
        facturaParaAnular.setPrecioTotal(2000.0);
        facturaParaAnular.setTipo(TipoComprobante.B);
        
        List<DetalleFactura> detalles = new ArrayList<>();
        detalles.add(detalleFactura1);
        detalles.add(detalleFactura2);
        facturaParaAnular.setDetalles(detalles);
    }

    @Test
    @DisplayName("Debe generar una Nota de Crédito copiando datos de la Factura")
    public void altaNotaPorFactura() {
        String motivo = "Error en la facturación del servicio";

        when(notaRepositorio.save(any(NotaCredito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotaCredito resultado = notaServicio.altaNotaPorFactura(facturaParaAnular, motivo);

        assertNotNull(resultado);
        
        assertEquals(facturaParaAnular.getPrecioTotal(), resultado.getPrecioTotal(), "El precio total debe coincidir");
        assertEquals(facturaParaAnular.getTipo(), resultado.getTipo(), "El tipo de comprobante debe coincidir");
        assertEquals(facturaParaAnular, resultado.getFacturaAnulada(), "Debe estar vinculada a la factura original");
        assertEquals(motivo, resultado.getMotivoAnulacion());
        assertEquals("Admin Hardcodeado", resultado.getEmpleadoResponsable(), "Debe asignar el responsable por defecto");
        assertNotNull(resultado.getFecha(), "La fecha no debe ser nula");

        verify(notaRepositorio, times(1)).save(any(NotaCredito.class));
    }

    @Test
    @DisplayName("Debe generar los DetalleNota correspondientes a cada DetalleFactura")
    public void altaNotaVerificarDetalles() {
        when(notaRepositorio.save(any(NotaCredito.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NotaCredito resultado = notaServicio.altaNotaPorFactura(facturaParaAnular, "Cancelación");
        assertNotNull(resultado.getDetallesNota());
        assertEquals(2, resultado.getDetallesNota().size(), "Debe tener la misma cantidad de detalles que la factura");

        var detNota1 = resultado.getDetallesNota().get(0);
        assertEquals("Servicio de Internet", detNota1.getDescripcion());
        assertEquals(1500.0, detNota1.getPrecio());
        assertEquals(detalleFactura1, detNota1.getDetalleFactura()); 
        assertEquals(resultado, detNota1.getNotaCredito()); 

        var detNota2 = resultado.getDetallesNota().get(1);
        assertEquals("Instalación cableada", detNota2.getDescripcion());
        assertEquals(500.0, detNota2.getPrecio());
    }
}
