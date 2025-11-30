package integrador.programa.controladores;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


@Controller
public class HomeControlador extends Object {

    @GetMapping("/")
    public String mostrarPaginaInicio() {
        return "inicio";
    }



}
