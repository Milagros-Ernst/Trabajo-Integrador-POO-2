package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import integrador.programa.servicios.NotaServicio;

@Controller
public class NotaControlador {
    @Autowired
    NotaServicio notaServicio;

    public NotaControlador(NotaServicio notaServicio){
        this.notaServicio = notaServicio;
    }
}
