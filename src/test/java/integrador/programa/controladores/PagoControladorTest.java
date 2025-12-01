package integrador.programa.controladores;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.PagoServicio;

@WebMvcTest(PagoControlador.class)
public class PagoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PagoServicio pagoServicio;
    @MockBean
    private ClienteService clienteService;
    @MockBean
    private FacturaServicio facturaServicio;

    private Cliente cliente;
    private Factura factura;
    private List<Cliente> listaClientes;
    private List<Factura> listaFacturas;

    @BeforeEach
    public void iniciar() {
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setNumeroDocumento("12345678");

        factura = new Factura();
        ReflectionTestUtils.setField(factura, "idFactura", 100L);
        factura.setCliente(cliente);
        factura.setEstado(EstadoFactura.VIGENTE);
        factura.setPrecioTotal(1000.0);
        
        ReflectionTestUtils.setField(factura, "detallesRecibo", new ArrayList<>());

        listaClientes = new ArrayList<>();
        listaClientes.add(cliente);

        listaFacturas = new ArrayList<>();
        listaFacturas.add(factura);
    }

    @Test
    @DisplayName("Debe mostrar la pantalla inicial de administrar pagos")
    public void irAAdministrarPagosSinSeleccion() throws Exception {
        when(clienteService.listarClientesActivos()).thenReturn(listaClientes);

        mockMvc.perform(get("/administrar-pagos"))
                .andExpect(status().isOk())
                .andExpect(view().name("administrar-pagos"))
                .andExpect(model().attributeExists("clientes"));
    }

    @Test
    @DisplayName("Debe mostrar la pantalla de pagos con facturas pendientes")
    public void irAAdministrarPagosConClienteSeleccionado() throws Exception {
        Long idCliente = 1L;
        when(clienteService.listarClientesActivos()).thenReturn(listaClientes);
        when(clienteService.buscarPorId(idCliente)).thenReturn(cliente);
        when(facturaServicio.buscarFacturasPorCliente(cliente)).thenReturn(listaFacturas);

        mockMvc.perform(get("/administrar-pagos")
                        .param("clienteId", String.valueOf(idCliente)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("administrar-pagos"))
                .andExpect(model().attributeExists("clienteSeleccionado", "facturasPendientes"))
                .andExpect(model().attribute("facturasPendientes", hasSize(1)));
    }

    @Test
    @DisplayName("Debe preparar el pago correctamente")
    public void prepararPagoExitoso() throws Exception {
        Long idCliente = 1L;
        Long idFactura = 100L;

        when(clienteService.buscarPorId(idCliente)).thenReturn(cliente);
        when(facturaServicio.buscarPorId(idFactura)).thenReturn(Optional.of(factura));

        mockMvc.perform(post("/administrar-pagos/preparar")
                        .param("clienteId", String.valueOf(idCliente))
                        .param("facturasIds", String.valueOf(idFactura)))
                .andExpect(status().isOk())
                .andExpect(view().name("procesar-pago"))
                .andExpect(model().attribute("totalCalculado", 1000.0));
    }


    @Test
    @DisplayName("Debe manejar errores y volver a la pantalla de administración")
    public void finalizarPagoConError() throws Exception {
        doThrow(new IllegalArgumentException("Error simulado"))
            .when(pagoServicio).registrarPagoMasivo(anyList(), any(), any(), any(), any());

        mockMvc.perform(post("/administrar-pagos/finalizar")
                        .param("clienteId", "1")
                        .param("facturasIds", "100")
                        .param("montoPagar", "1000.0")
                        .param("metodoPago", "EFECTIVO"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/administrar-pagos?clienteId=1"))
                .andExpect(flash().attributeExists("error"));
    }
}