package integrador.programa.controladores;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import integrador.programa.modelo.*;
import integrador.programa.modelo.enumeradores.TipoIVA;
import integrador.programa.repositorios.NotaRepositorio;
import integrador.programa.servicios.NotaServicio;

@WebMvcTest(NotaControlador.class)
public class NotaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotaServicio notaServicio;

    @MockBean
    private NotaRepositorio notaRepositorio;

    private NotaCredito notaCredito;
    private Factura factura;
    private Cliente cliente;

    @BeforeEach
    public void iniciar() {
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        cliente.setCondIVA(integrador.programa.modelo.enumeradores.CondicionIVA.CONSUMIDOR_FINAL);
        cliente.setDireccion("Calle Falsa 123");
        cliente.setMail("maria@test.com");
        cliente.setNumeroDocumento("12345678");

        factura = new Factura();
        ReflectionTestUtils.setField(factura, "idFactura", 10L);
        factura.setCliente(cliente);
        factura.setFecha(java.time.LocalDate.now()); 

        notaCredito = new NotaCredito();
        ReflectionTestUtils.setField(notaCredito, "nroNota", 50L);
        notaCredito.setFacturaAnulada(factura);
        notaCredito.setFecha(java.time.LocalDate.now());
        notaCredito.setEmpleadoResponsable("Tester");
        notaCredito.setTipo(integrador.programa.modelo.enumeradores.TipoComprobante.B);
        notaCredito.setMotivoAnulacion("Error de facturación"); 
        
        Servicio servicio = new Servicio();
        servicio.setTipoIva(TipoIVA.IVA_21); 

        DetalleFactura detalleFactura = new DetalleFactura();
        detalleFactura.setServicio(servicio);
        detalleFactura.setFactura(factura); 
        
        DetalleNota detalleNota = new DetalleNota();
        detalleNota.setPrecio(100.0);
        detalleNota.setDescripcion("Servicio de Internet");
        detalleNota.setDetalleFactura(detalleFactura);
        
        List<DetalleNota> detalles = new ArrayList<>();
        detalles.add(detalleNota);
        notaCredito.setDetallesNota(detalles);
        
        notaCredito.setPrecioTotal(100.0); 
    }

    @Test
    @DisplayName("Debe mostrar el detalle de la nota de crédito y calcular IVAs correctamente")
    public void verNotaDeCreditoExito() throws Exception {
        Long idFactura = 10L;
        List<NotaCredito> notasEncontradas = List.of(notaCredito);

        when(notaRepositorio.findByFacturaAnulada_IdFactura(idFactura)).thenReturn(notasEncontradas);

        mockMvc.perform(get("/clientes/facturacion/nota-credito/{idFactura}", idFactura))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("notacredito-detalle"))
                .andExpect(model().attributeExists("nota", "cliente", "subtotalNeto", "ivaDesglosado"))
                .andExpect(model().attribute("nota", notaCredito))
                .andExpect(model().attribute("cliente", cliente))
                .andExpect(model().attribute("subtotalNeto", 100.0))
                .andExpect(model().attribute("ivaDesglosado", hasKey("21.0%")));
    }

    @Test
    @DisplayName("Debe redirigir a clientes si no existe nota de crédito para la factura indicada")
    public void verNotaDeCreditoNoExiste() throws Exception {
        Long idFacturaInexistente = 999L;
        
        when(notaRepositorio.findByFacturaAnulada_IdFactura(idFacturaInexistente)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/clientes/facturacion/nota-credito/{idFactura}", idFacturaInexistente))
                .andDo(print())
                .andExpect(status().is3xxRedirection()) 
                .andExpect(view().name("redirect:/clientes"));
    }
}