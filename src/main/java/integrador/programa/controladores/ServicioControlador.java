package integrador.programa.controladores;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
//import integrador.programa.servicios.ServicioServicio; // Servicio de negocio para Servicio

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller                                  // Controlador MVC (devuelve vistas HTML)
@RequestMapping("/servicios")                // Ruta base para la Gestión de Servicios
public class ServicioControlador {

    /*private final ServicioServicio servicioServicio;

    // Inyección del servicio por constructor
    public ServicioControlador(ServicioServicio servicioServicio) {
        this.servicioServicio = servicioServicio;
    }*/

    // GET /servicios Muestra la pantalla "Gestión de Servicios"
    @GetMapping
    public String mostrarGestionServicios(Model model) {

        // 1) Obtenemos todos los servicios para la tabla
        //  List<Servicio> servicios = servicioServicio.listarTodos();

        // 2) Creamos un objeto vacío para vincular al formulario de alta
        Servicio servicioForm = new Servicio();

        // 3) Agregamos al modelo: el formulario, la lista de servicios, los tipos de IVA para el combo
        model.addAttribute("servicioForm", servicioForm);
       // model.addAttribute("listaServicios", servicios);
        model.addAttribute("tiposIva", TipoIVA.values());

        // 4) Nombre de la vista (ej: gestion-servicios.html)
        return "gestion-servicios";
    }

    // POST /servicios  (Alta de Servicio)
    @PostMapping
    public String guardarServicio(
            @Valid
            @ModelAttribute("servicioForm") Servicio servicioForm, // Datos del form
            BindingResult bindingResult,                           // Resultado validaciones
            Model model) {

        // 1) Si hay errores de validación...
        if (bindingResult.hasErrors()) {

            // Volvemos a cargar la lista y los tipos de IVA
            // para que la vista se muestre completa
          //  List<Servicio> servicios = servicioServicio.listarTodos();
          //  model.addAttribute("listaServicios", servicios);
            model.addAttribute("tiposIva", TipoIVA.values());

            // Se queda en la misma vista para mostrar errores
            return "gestion-servicios";
        }

        // 2) Si no hay errores, se guarda el servicio
       // servicioServicio.crearServicio(servicioForm);

        // 3) Redirige a GET /servicios para evitar doble envío del formulario
        return "redirect:/servicios";
    }
}
