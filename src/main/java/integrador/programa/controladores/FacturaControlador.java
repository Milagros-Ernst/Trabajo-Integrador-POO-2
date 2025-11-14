package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import integrador.programa.servicios.FacturaServicio;
import jakarta.validation.Valid;
import integrador.programa.modelo.*;

@RestController
public class FacturaControlador {
    
    @Autowired
    FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio){
        this.facturaServicio = facturaServicio;
    }

    @PostMapping("/facturas")
    public String altaFactura (@Valid Factura factura, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return "nuevaFactura";
        }
        facturaServicio.agregarFactura(factura);
        return "redirect:/facturas";    
    }

    
}
