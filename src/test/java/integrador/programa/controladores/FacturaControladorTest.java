package integrador.programa.controladores;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.PagoServicio;
import integrador.programa.servicios.ServicioServicio;

@WebMvcTest(FacturaControlador.class)
public class FacturaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FacturaServicio facturaServicio;
    @MockBean
    private ClienteService clienteService;
    @MockBean
    private ServicioServicio servicioServicio;
    @MockBean
    private ClienteServicioServicio clienteServicioServicio;
    @MockBean
    private PagoServicio pagoServicio;

    private Factura factura;
    private Cliente cliente;
    private List<Servicio> listaServicios;
    private List<Cliente> listaClientes;

    @BeforeEach
    public void iniciar() {
        // Inicio datos cliente
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");

        // Inicio datos factura
        factura = new Factura();
        ReflectionTestUtils.setField(factura, "idFactura", 100L);
        factura.setCliente(cliente);
        factura.setEstado(EstadoFactura.VIGENTE);
        factura.setPrecioTotal(1000.0);

        // Inicio lista de servicios
        listaServicios = new ArrayList<>();
        Servicio s1 = new Servicio();
        s1.setNombre("Internet");
        listaServicios.add(s1);

        // Inicio lista clientes
        listaClientes = new ArrayList<>();
        listaClientes.add(cliente);
    }

    @Test
    @DisplayName("Debe mostrar la página de inicio de facturación")
    public void accesoIrAFacturacion() throws Exception {
        mockMvc.perform(get("/facturacion"))
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(FacturaControlador.class))
            .andExpect(handler().methodName("irAFacturacion"))
            .andExpect(view().name("facturacion-inicio"));
    }

    @Test
    @DisplayName("Debe mostrar la página de facturación masiva con la lista de servicios")
    public void accesoIrAFacturacionMasiva() throws Exception {
        when(servicioServicio.listarTodos()).thenReturn(listaServicios);

        mockMvc.perform(get("/facturacion/masiva"))
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(FacturaControlador.class))
            .andExpect(handler().methodName("irAFacturacionMasiva"))
            .andExpect(model().attributeExists("servicios"))
            .andExpect(model().attribute("servicios", hasSize(1)))
            .andExpect(view().name("facturacion-masiva"));
    }

    @Test
    @DisplayName("Debe mostrar la página de facturación individual con la lista de clientes")
    public void accesoIrAFacturacionIndividual() throws Exception {
        when(clienteService.listarClientesActivos()).thenReturn(listaClientes);

        mockMvc.perform(get("/facturacion/individual"))
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(FacturaControlador.class))
            .andExpect(handler().methodName("irAFacturacionIndividual"))
            .andExpect(model().attributeExists("clientes"))
            .andExpect(model().attribute("clientes", hasSize(1)))
            .andExpect(view().name("facturacion-individual"));
    }

    @Test
    @DisplayName("Debe anular una factura y redirigir al historial del cliente")
    public void anularFactura() throws Exception {
        when(facturaServicio.buscarPorId(100L)).thenReturn(Optional.of(factura));
        
        mockMvc.perform(post("/facturacion/anular/{id}", 100L)
                .param("motivo", "Error de carga"))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/clientes/1/facturacion")); 
    }
    
    @Test
    @DisplayName("Debe redirigir con error si falla la anulación")
    public void anularFacturaConError() throws Exception {
        when(facturaServicio.buscarPorId(100L)).thenReturn(Optional.of(factura));
        
        when(facturaServicio.bajaFactura(100L, "Error")).thenThrow(new IllegalStateException("No se puede anular"));

        mockMvc.perform(post("/facturacion/anular/{id}", 100L)
                .param("motivo", "Error"))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clientes/1/facturacion?error=No se puede anular"));
    }
}