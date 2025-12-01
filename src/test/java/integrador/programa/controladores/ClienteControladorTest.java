package integrador.programa.controladores;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import integrador.programa.modelo.enumeradores.CondicionIVA;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import integrador.programa.servicios.ClienteService;
import integrador.programa.servicios.ClienteServicioServicio;
import integrador.programa.servicios.FacturaServicio;
import integrador.programa.servicios.PagoServicio;

@WebMvcTest(ClienteControlador.class)
public class ClienteControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClienteService clienteService;
    
    @MockBean
    private ClienteServicioServicio clienteServicioServicio;
    
    @MockBean
    private FacturaServicio facturaServicio;
    
    @MockBean
    private PagoServicio pagoServicio;

    // Atributos de prueba
    private Cliente cliente;
    private List<Cliente> listaClientesActivos;
    private List<Cliente> listaClientesInactivos;

    @BeforeEach
    public void iniciar() {
        cliente = new Cliente();
        ReflectionTestUtils.setField(cliente, "idCuenta", 1L);
        cliente.setNombre("Maria");
        cliente.setApellido("Gomez");
        cliente.setTipoDocumento(TipoDocumento.DNI);
        cliente.setNumeroDocumento("12345678");
        cliente.setCondIVA(CondicionIVA.CONSUMIDOR_FINAL);
        cliente.setMail("maria@test.com");
        cliente.setTelefono("123456");
        cliente.setDireccion("Calle Falsa 123");
        cliente.setDireccionFiscal("Calle Fiscal 456");

        listaClientesActivos = new ArrayList<>();
        listaClientesActivos.add(cliente);

        listaClientesInactivos = new ArrayList<>();
    }

    @Test
    @DisplayName("Debe mostrar la página de inicio de clientes")
    public void accesoIrAInicioClientes() throws Exception {
        mockMvc.perform(get("/clientes"))
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(ClienteControlador.class))
            .andExpect(handler().methodName("irAInicioClientes"))
            .andExpect(view().name("clientes-inicio"));
    }

    @Test
    @DisplayName("Debe mostrar la página de gestión con las tablas de clientes")
    public void accesoIrAGestionClientes() throws Exception {
        when(clienteService.listarClientesActivos()).thenReturn(listaClientesActivos);
        when(clienteService.listarClientesInactivos()).thenReturn(listaClientesInactivos);

        mockMvc.perform(get("/clientes/gestion"))
            .andExpect(status().isOk())
            .andExpect(handler().handlerType(ClienteControlador.class))
            .andExpect(handler().methodName("irAClientes"))
            .andExpect(model().attributeExists("clientes", "clientesInactivos", "cliente"))
            .andExpect(model().attribute("clientes", hasSize(1)))
            .andExpect(view().name("gestion-clientes-inicio"));
    }

    @Test
    @DisplayName("Debe crear un cliente nuevo exitosamente")
    public void crearClienteExitoso() throws Exception {
        when(clienteService.crearCliente(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/clientes/gestion")
                .param("nombre", "Maria")
                .param("apellido", "Gomez")
                .param("numeroDocumento", "12345678")
                .param("tipoDocumento", "DNI")
                .param("condIVA", "CONSUMIDOR_FINAL")
                .param("mail", "maria@test.com")
                .param("telefono", "123456")
                .param("direccion", "Calle Falsa 123")
                .param("direccionFiscal", "Calle Fiscal 456"))
            .andDo(print())
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clientes/gestion"));
    }

    @Test
    @DisplayName("Debe mostrar la página de detalle de un cliente específico")
    public void irADetalleCliente() throws Exception {
        Long idCliente = 1L;
        
        when(clienteService.buscarPorId(idCliente)).thenReturn(cliente);
        when(clienteService.listarClientesActivos()).thenReturn(listaClientesActivos); 
        when(clienteServicioServicio.listarServiciosActivosDeCliente(idCliente)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/clientes/gestion/{id}", idCliente))
            .andExpect(status().isOk())
            .andExpect(view().name("gestion-clientes-detalle"))
            .andExpect(model().attributeExists("cliente", "subClientes", "serviciosContratados"))
            .andExpect(model().attribute("cliente", cliente));
    }

    @Test
    @DisplayName("Debe modificar un cliente correctamente")
    public void modificarCliente() throws Exception {
        Long idCliente = 1L;
        
        when(clienteService.modificarCliente(eq(idCliente), any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put("/clientes/gestion/{id}", idCliente)
                .param("nombre", "Maria Modificada")
                .param("apellido", "Gomez")
                .param("numeroDocumento", "12345678")
                .param("tipoDocumento", "DNI"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clientes/gestion/1")); 
    }

    @Test
    @DisplayName("Debe dar de baja un cliente")
    public void darDeBajaCliente() throws Exception {
        Long idCliente = 1L;
        
        when(clienteService.bajaCliente(idCliente)).thenReturn(cliente);

        mockMvc.perform(post("/clientes/{id}/dar-de-baja", idCliente))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clientes/gestion"));
    }

    @Test
    @DisplayName("Debe reactivar un cliente inactivo")
    public void reactivarCliente() throws Exception {
        Long idCliente = 1L;
        
        when(clienteService.reactivarCliente(idCliente)).thenReturn(cliente);

        mockMvc.perform(post("/clientes/gestion/{id}/reactivar", idCliente))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/clientes/gestion"))
            .andExpect(flash().attributeExists("mensaje"));
    }
}