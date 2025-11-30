package integrador.programa.controladores;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.DetalleRecibo;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Recibo;
import integrador.programa.modelo.enumeradores.CondicionIVA;
import integrador.programa.modelo.enumeradores.MetodoPago;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import integrador.programa.servicios.ReciboServicio;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/recibos")
public class ReciboControlador {

    private final ReciboServicio reciboServicio;

    public ReciboControlador(ReciboServicio reciboServicio) {
        this.reciboServicio = reciboServicio;
    }

    //   VER RECIBO REAL POR ID (Long)
    @GetMapping("/{idRecibo}")
    public String verReciboPorId(@PathVariable Long idRecibo, Model model) {
        Recibo recibo = reciboServicio.buscarPorId(idRecibo);
        model.addAttribute("recibo", recibo);
        model.addAttribute("cliente", recibo.getCliente());
        return "recibo-detalle";
    }

    //   VER RECIBO REAL POR NÚMERO
    @GetMapping("/numero/{nroRecibo}")
    public String verReciboPorNumero(@PathVariable Long nroRecibo, Model model) {
        Recibo recibo = reciboServicio.buscarPorNumero(nroRecibo);
        model.addAttribute("recibo", recibo);
        model.addAttribute("cliente", recibo.getCliente());
        return "recibo-detalle";
    }

}
