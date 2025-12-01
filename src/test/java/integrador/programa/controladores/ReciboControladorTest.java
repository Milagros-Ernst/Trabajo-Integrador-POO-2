package integrador.programa.controladores;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Recibo;
import integrador.programa.servicios.ReciboServicio;

@WebMvcTest(ReciboControlador.class)
public class ReciboControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReciboServicio reciboServicio;

    private Recibo recibo;
    private Cliente cliente;

    @BeforeEach
    public void iniciar() {
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setNumeroDocumento("30123456789");
        
        recibo = new Recibo();
        ReflectionTestUtils.setField(recibo, "idRecibo", 100L);
        recibo.setNroRecibo(5000L); // Número lógico del comprobante
        recibo.setFechaEmision(LocalDate.now());
        recibo.setImporteTotal(1500.0);
        recibo.setCliente(cliente);
    }

    @Test
    @DisplayName("Debe mostrar el detalle del recibo cuando se busca por ID interno")
    public void verReciboPorId() throws Exception {
        Long idBuscado = 100L;
        
        when(reciboServicio.buscarPorId(idBuscado)).thenReturn(recibo);

        mockMvc.perform(get("/recibos/{id}", idBuscado))
            .andDo(print())
            .andExpect(status().isOk()) // Código 200
            .andExpect(view().name("recibo-detalle")) // Nombre del HTML
            .andExpect(model().attributeExists("recibo", "cliente"))
            .andExpect(model().attribute("recibo", hasProperty("idRecibo", is(100L))))
            .andExpect(model().attribute("cliente", hasProperty("nombre", is("Juan"))));
    }

    @Test
    @DisplayName("Debe mostrar el detalle del recibo cuando se busca por Número de Comprobante")
    public void verReciboPorNumero() throws Exception {
        Long nroComprobante = 5000L;

        when(reciboServicio.buscarPorNumero(nroComprobante)).thenReturn(recibo);

        mockMvc.perform(get("/recibos/numero/{nro}", nroComprobante))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(view().name("recibo-detalle"))
            .andExpect(model().attribute("recibo", hasProperty("nroRecibo", is(5000L))));
    }
}