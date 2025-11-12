package integrador.programa.controladores;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class hola {
    @GetMapping("/")
    public String hola() {
        return "holaaaaa";
    }
}
