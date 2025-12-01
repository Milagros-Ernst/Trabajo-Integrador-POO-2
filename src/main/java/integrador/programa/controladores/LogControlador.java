package integrador.programa.controladores;

import integrador.programa.repositorios.LogFacturacionMasRepositorio;
import integrador.programa.repositorios.ServicioRepositorio;
import integrador.programa.modelo.LogFacturacionMasiva;
import integrador.programa.modelo.Servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/facturacion/masiva/log")
public class LogControlador {

    @Autowired
    private LogFacturacionMasRepositorio logRepositorio;
    @Autowired
    private ServicioRepositorio servicioRepositorio;

    @GetMapping
    public String verLogMasiva(Model model,
                               @RequestParam(required = false) Integer periodo,
                               @RequestParam(required = false) LocalDate fecha) {
        
        List<LogFacturacionMasiva> logs;

        if (periodo != null && fecha != null) {
            // Filtrar por ambos
            logs = logRepositorio.findByPeriodoAndFechaEjecucionOrderByIdDesc(periodo, fecha);
        } else if (periodo != null) {
            logs = logRepositorio.findByPeriodoOrderByFechaEjecucionDesc(periodo);
        } else if (fecha != null) {
            logs = logRepositorio.findByFechaEjecucionOrderByIdDesc(fecha);
        } else {
            // Sin 
            logs = logRepositorio.findAll(Sort.by(Sort.Direction.DESC, "fechaEjecucion"));
        }

        List<Servicio> todosLosServicios = servicioRepositorio.findAll();

        Map<String, String> mapaIdANombre = todosLosServicios.stream()
                .collect(Collectors.toMap(
                        s -> String.valueOf(s.getIdServicio()),
                        Servicio::getNombre
                ));

        Map<Long, String> nombresPorLog = new HashMap<>();
        for (LogFacturacionMasiva log : logs) {
            String rawIds = log.getServiciosIncluidos();

            if (rawIds != null && !rawIds.isEmpty()) {
                String[] ids = rawIds.split(",");
                List<String> nombresList = new ArrayList<>();

                for (String id : ids) {
                    String idLimpio = id.trim();
                    String nombreReal = mapaIdANombre.getOrDefault(idLimpio, "Servicio eliminado (ID:" + idLimpio + ")");
                    nombresList.add(nombreReal);
                }
                nombresPorLog.put(log.getId(), String.join(", ", nombresList));
            } else {
                nombresPorLog.put(log.getId(), "Sin servicios");
            }
        }

        model.addAttribute("logs", logs);
        model.addAttribute("mapaNombresServicios", nombresPorLog);
        model.addAttribute("periodoSeleccionado", periodo);
        model.addAttribute("fechaSeleccionada", fecha);
        model.addAttribute("periodosFiltro", generarListaPeriodos()); 

        return "facturacion-masiva-logs";
    }

    private List<Map<String, Object>> generarListaPeriodos() {
        List<Map<String, Object>> periodos = new ArrayList<>();
        LocalDate fecha = LocalDate.now();
        
        String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                                 "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        for (int i = 0; i < 12; i++) {
            LocalDate fechaIteracion = fecha.plusMonths(i);
            
            int mes = fechaIteracion.getMonthValue();
            int anio = fechaIteracion.getYear();      
            int anioCorto = anio % 100;               

            int valorPeriodo = (mes * 100) + anioCorto;

            String etiqueta = nombresMeses[mes - 1] + " " + anio;

            Map<String, Object> item = new HashMap<>();
            item.put("value", valorPeriodo);
            item.put("text", etiqueta);
            periodos.add(item);
        }
        return periodos;
    }
}