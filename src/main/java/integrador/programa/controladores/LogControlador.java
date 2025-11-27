package integrador.programa.controladores;
import integrador.programa.repositorios.LogFacturacionMasRepositorio;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import integrador.programa.modelo.*;

import java.util.List;


@Controller
@RequestMapping("/facturacion/masiva/log")
public class LogControlador {

    @Autowired
    private LogFacturacionMasRepositorio logRepositorio;

    @GetMapping
    public String verLogMasiva(Model model) {
        // buscamos todos los logs, idealmente ordenados por fecha descendente
        List<LogFacturacionMasiva> logs = logRepositorio.findAll(Sort.by(Sort.Direction.DESC, "fechaEjecucion"));

        model.addAttribute("logs", logs);
        return "facturacion-masiva-logs";
    }

}
