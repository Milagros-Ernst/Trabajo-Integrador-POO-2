package integrador.programa.controladores;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import integrador.programa.modelo.LogFacturacionMasiva;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
import integrador.programa.repositorios.LogFacturacionMasRepositorio;
import integrador.programa.repositorios.ServicioRepositorio;

@WebMvcTest(LogControlador.class)
public class LogControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LogFacturacionMasRepositorio logRepositorio;

    @MockBean
    private ServicioRepositorio servicioRepositorio;

    private LogFacturacionMasiva log1;
    private Servicio servicioInternet;

    @BeforeEach
    public void iniciar() {
        servicioInternet = new Servicio();
        ReflectionTestUtils.setField(servicioInternet, "idServicio", "serv-001");
        servicioInternet.setNombre("Internet 50MB");
        servicioInternet.setPrecioUnitario(1000.0);
        servicioInternet.setTipoIva(TipoIVA.IVA_21);
        servicioInternet.setEstadoServicio(EstadoServicio.ALTA);

        log1 = new LogFacturacionMasiva(10, 50, "serv-001", "Admin Test");
        ReflectionTestUtils.setField(log1, "id", 1L); // ID necesario para el mapa del controller
        ReflectionTestUtils.setField(log1, "fechaEjecucion", LocalDate.now());
    }

    @Test
    @DisplayName("Debe mostrar el historial completo sin filtros")
    public void verLogSinFiltros() throws Exception {
        when(logRepositorio.findAll(any(Sort.class))).thenReturn(List.of(log1));
        when(servicioRepositorio.findAll()).thenReturn(List.of(servicioInternet));

        mockMvc.perform(get("/facturacion/masiva/log"))
            .andExpect(status().isOk())
            .andExpect(view().name("facturacion-masiva-logs"))
            .andExpect(model().attributeExists("logs", "mapaNombresServicios"))
            .andExpect(model().attribute("logs", hasSize(1)));
        
        verify(logRepositorio).findAll(any(Sort.class));
    }

    @Test
    @DisplayName("Debe filtrar correctamente por periodo")
    public void verLogFiltradoPorPeriodo() throws Exception {
        int periodo = 5;
        when(logRepositorio.findByPeriodoOrderByFechaEjecucionDesc(periodo)).thenReturn(List.of(log1));
        when(servicioRepositorio.findAll()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/facturacion/masiva/log")
                .param("periodo", String.valueOf(periodo)))
            .andExpect(status().isOk())
            .andExpect(model().attribute("logs", hasSize(1)))
            .andExpect(model().attribute("periodoSeleccionado", periodo)); 

        verify(logRepositorio).findByPeriodoOrderByFechaEjecucionDesc(periodo);
    }

    @Test
    @DisplayName("Debe mapear correctamente los IDs de servicios a sus Nombres reales")
    public void verificarMapeoDeNombresDeServicios() throws Exception {
        
        when(logRepositorio.findAll(any(Sort.class))).thenReturn(List.of(log1));
        
        when(servicioRepositorio.findAll()).thenReturn(List.of(servicioInternet));

        mockMvc.perform(get("/facturacion/masiva/log"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("mapaNombresServicios", hasEntry(1L, "Internet 50MB")));
    }
    
    @Test
    @DisplayName("Debe manejar el caso de servicios eliminados o no encontrados en el mapeo")
    public void verificarMapeoConServicioInexistente() throws Exception {
        LogFacturacionMasiva logFantasma = new LogFacturacionMasiva(1, 1, "id-fantasma", "Admin");
        ReflectionTestUtils.setField(logFantasma, "id", 2L);
        
        when(logRepositorio.findAll(any(Sort.class))).thenReturn(List.of(logFantasma));
        when(servicioRepositorio.findAll()).thenReturn(new ArrayList<>()); // Lista vacía

        mockMvc.perform(get("/facturacion/masiva/log"))
            .andExpect(status().isOk())
            .andExpect(model().attribute("mapaNombresServicios", 
                    hasEntry(2L, "Servicio eliminado (ID:id-fantasma)")));
    }
}