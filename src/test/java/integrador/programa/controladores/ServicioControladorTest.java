package integrador.programa.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
import integrador.programa.servicios.ServicioServicio;

@WebMvcTest(ServicioControlador.class)
public class ServicioControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ServicioServicio servicioServicio;

    private Servicio servicio;
    private List<Servicio> listaServicios;

    @BeforeEach
    public void iniciar() {
        servicio = new Servicio();
        ReflectionTestUtils.setField(servicio, "idServicio", "uuid-123-test");
        servicio.setNombre("Mantenimiento PC");
        servicio.setDescripcion("Limpieza y optimización");
        servicio.setPrecioUnitario(5000.0);
        servicio.setTipoIva(TipoIVA.IVA_21);
        servicio.setEstadoServicio(EstadoServicio.ALTA);

        listaServicios = new ArrayList<>();
        listaServicios.add(servicio);
    }

    @Test
    @DisplayName("Debe mostrar la vista de gestión de servicios con la lista cargada")
    public void irAServicios() throws Exception {
        when(servicioServicio.listarTodos()).thenReturn(listaServicios);

        mockMvc.perform(get("/servicios"))
                .andExpect(status().isOk())
                .andExpect(view().name("gestion-servicio-abm")) 
                .andExpect(model().attributeExists("servicios"))
                .andExpect(model().attribute("servicios", hasSize(1)))
                .andExpect(model().attribute("servicios", hasItem(
                        allOf(
                                hasProperty("nombre", is("Mantenimiento PC")),
                                hasProperty("precioUnitario", is(5000.0))
                        )
                )));
    }

    @Test
    @DisplayName("Debe crear un servicio correctamente y redirigir")
    public void crearServicioExitoso() throws Exception {
        when(servicioServicio.agregarServicio(any(Servicio.class))).thenReturn(servicio);

        mockMvc.perform(post("/servicios")
                        .param("nombre", "Instalación Red")
                        .param("descripcion", "Cableado estructurado")
                        .param("precioUnitario", "15000")
                        .param("tipoIva", "IVA_21"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicios")); 

        verify(servicioServicio).agregarServicio(any(Servicio.class));
    }

    @Test
    @DisplayName("Debe manejar errores al crear servicio (catch) y volver al formulario")
    public void crearServicioConError() throws Exception {
        doThrow(new IllegalArgumentException("Datos inválidos"))
                .when(servicioServicio).agregarServicio(any(Servicio.class));
        
        when(servicioServicio.listarTodos()).thenReturn(listaServicios);

        mockMvc.perform(post("/servicios")
                        .param("nombre", "Error Service")
                        .param("precioUnitario", "100")
                        .param("tipoIva", "IVA_21"))
                .andExpect(status().isOk()) 
                .andExpect(view().name("gestion-servicio-abm"))
                .andExpect(model().attributeExists("error")) 
                .andExpect(model().attributeExists("servicios")); 
    }

    @Test
    @DisplayName("Debe modificar un servicio existente")
    public void modificarServicio() throws Exception {
        String id = "uuid-123-test";
        
        when(servicioServicio.actualizarServicio(eq(id), any(Servicio.class))).thenReturn(servicio);

        mockMvc.perform(post("/servicios/editar/{id}", id)
                        .param("nombre", "Mantenimiento Modificado")
                        .param("descripcion", "Nueva desc")
                        .param("precioUnitario", "6000")
                        .param("tipoIva", "IVA_105"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicios"));
        
        verify(servicioServicio).actualizarServicio(eq(id), any(Servicio.class));
    }

    @Test
    @DisplayName("Debe dar de baja un servicio")
    public void bajaServicio() throws Exception {
        String id = "uuid-123-test";
        
        mockMvc.perform(post("/servicios/{id}", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/servicios"));

        verify(servicioServicio).eliminarServicio(id);
    }
}