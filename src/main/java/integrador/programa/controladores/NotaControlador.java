package integrador.programa.controladores;

import integrador.programa.modelo.DetalleNota;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.repositorios.NotaRepositorio;
import integrador.programa.servicios.NotaServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes/facturacion")
public class NotaControlador {

    @Autowired
    NotaServicio notaServicio;

    @Autowired
    NotaRepositorio notaRepositorio; // Agregado para búsqueda rápida por ID

    public NotaControlador(NotaServicio notaServicio, NotaRepositorio notaRepositorio){
        this.notaServicio = notaServicio;
        this.notaRepositorio = notaRepositorio;
    }

    @GetMapping("/nota-credito/{idFactura}")
    public String verNotaDeCredito(@PathVariable Long idFactura, Model model) {

        List<NotaCredito> notas = notaRepositorio.findByFacturaAnulada_IdFactura(idFactura);

        if (notas != null && !notas.isEmpty()) {
            NotaCredito nota = notas.get(0);

            model.addAttribute("nota", nota);
            model.addAttribute("cliente", nota.getFacturaAnulada().getCliente());

            double subtotalNeto = nota.getDetallesNota().stream()
                    .mapToDouble(DetalleNota::getPrecio)
                    .sum();
            model.addAttribute("subtotalNeto", subtotalNeto);

            Map<String, Double> ivaDesglosado = new HashMap<>();

            for (DetalleNota detalle : nota.getDetallesNota()) {
                String alicuotaStr = detalle.getDetalleFactura().getAlicuotaIva();
                double porcentaje = Double.parseDouble(alicuotaStr.replace("%", "")) / 100;
                double montoIva = detalle.getPrecio() * porcentaje;

                ivaDesglosado.put(alicuotaStr, ivaDesglosado.getOrDefault(alicuotaStr, 0.0) + montoIva);
            }

            model.addAttribute("ivaDesglosado", ivaDesglosado);

            return "notacredito-detalle";
        } else {
            return "redirect:/clientes";
        }
    }
}