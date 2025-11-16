package integrador.programa.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import integrador.programa.repositorios.NotaRepositorio;

@Service
public class NotaServicio {
    @Autowired
    private final NotaRepositorio notaRepositorio;

    public NotaServicio(NotaRepositorio notaRepositorio) {
        this.notaRepositorio = notaRepositorio;
    }
}
