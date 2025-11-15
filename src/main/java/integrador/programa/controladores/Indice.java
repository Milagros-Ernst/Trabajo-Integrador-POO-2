package integrador.programa.controladores;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Indice {
    @GetMapping("/")
    public String hola() {
        return "holaaaaa";
    }
}