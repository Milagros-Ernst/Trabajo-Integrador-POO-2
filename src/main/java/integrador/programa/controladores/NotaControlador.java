package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;

import integrador.programa.servicios.NotaServicio;

public class NotaControlador {
    @Autowired
    NotaServicio notaServicio;

    public NotaControlador(NotaServicio notaServicio){
        this.notaServicio = notaServicio;
    }
}
