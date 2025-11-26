package integrador.programa.controladores;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.servicios.ServicioServicio;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller                   
@RequestMapping("/servicios")
public class ServicioControlador {

    private final ServicioServicio servicioServicio;

    public ServicioControlador(ServicioServicio servicioServicio) {
        this.servicioServicio = servicioServicio;
    }

    // metodos para la gestión de servicios
    @GetMapping
    public String irAServicios(Model model) {

        List<Servicio> misServicios = servicioServicio.listarTodos();

        model.addAttribute("servicios", misServicios);

        return "gestion-servicio-abm";
    }

    @PostMapping
    public String crearServicio(@ModelAttribute Servicio nuevoServicio, Model model) {
        try {
            nuevoServicio.setEstadoServicio(EstadoServicio.ALTA);
            servicioServicio.agregarServicio(nuevoServicio);
            return "redirect:/servicios";
        } catch (Exception e) {
            model.addAttribute("error", "Error al crear servicio: " + e.getMessage());
            model.addAttribute("servicios", servicioServicio.listarTodos());
            return "gestion-servicio-abm";
        }
    }

    // usamos post porque en el html thymeleaf tiene como metodo post (x el alta y el html es alta)
    // NO sabría como solucionar esto para que siga los lineamientos, mientras lo dejo así
    @PostMapping("/editar/{id}")
    public String modificarServicio(@PathVariable String id, @ModelAttribute Servicio servicioActualizado) {
        try {

            servicioServicio.actualizarServicio(id, servicioActualizado);

            return "redirect:/servicios";
        } catch (Exception e) {
            return "redirect:/servicios?error=" + e.getMessage();
        }

    }

    @PostMapping("/{id}")
    public String bajaServicio(@PathVariable String id) {
        try {
            servicioServicio.eliminarServicio(id);
            return "redirect:/servicios";
        } catch (Exception e) {
            return "redirect:/servicios?error=" + e.getMessage();
        }
    }
}
