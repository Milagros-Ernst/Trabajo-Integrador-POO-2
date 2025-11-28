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
    public String verLogMasiva(Model model) {
        // buscamos todos los logs, idealmente ordenados por fecha descendente
        List<LogFacturacionMasiva> logs = logRepositorio.findAll(Sort.by(Sort.Direction.DESC, "fechaEjecucion"));

        // para poder mostrar los nombres de servicio, y no simplemente las ids, mapeamos los servicios con sus ids
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

        return "facturacion-masiva-logs";
    }

}
