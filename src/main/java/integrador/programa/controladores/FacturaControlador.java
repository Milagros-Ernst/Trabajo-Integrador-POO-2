package integrador.programa.controladores;

// Imports existentes
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import integrador.programa.servicios.FacturaServicio;
import jakarta.validation.Valid;
import integrador.programa.modelo.*;

// --- IMPORTS NUEVOS PARA HU 11 ---
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import integrador.programa.controladores.dto.AnulacionRequestDTO;
// ---------------------------------

@RestController
public class FacturaControlador {
    
    @Autowired
    FacturaServicio facturaServicio; // Tu servicio existente

    public FacturaControlador(FacturaServicio facturaServicio){
        this.facturaServicio = facturaServicio;
    }

    /**
     * Endpoint existente para crear una factura (Formulario web)
     */
    @PostMapping("/facturas")
    public String altaFactura (@Valid Factura factura, BindingResult resultado) {
        if (resultado.hasErrors()) {
            return "nuevaFactura";
        }
        // Asumo que tu servicio tiene este método
        // facturaServicio.agregarFactura(factura); 
        return "redirect:/facturas";    
    }

    
    // --- ⬇️ INICIO: CÓDIGO NUEVO PARA HU 11 ⬇️ ---

    /**
     * Endpoint para anular una factura (Implementa HU 11).
     *
     * Se accede mediante un POST a: /facturas/{id}/anular
     * (Nota: la URL base es diferente a tu POST de alta)
     *
     * @param idFactura El ID (String UUID) de la factura a anular (viene de la URL).
     * @param request El JSON con el motivo y responsable (viene del Body).
     * @return Un ResponseEntity con la NotaCredito generada (JSON) y un estado HTTP 200 (OK).
     */
    @PostMapping("/facturas/{id}/anular")
    public ResponseEntity<NotaCredito> anularFactura(
            @PathVariable("id") String idFactura,
            @RequestBody AnulacionRequestDTO request) {
        
        // 1. Llama al método 'anularFactura' que debe estar en tu FacturaServicio
        NotaCredito notaCreditoGenerada = facturaServicio.anularFactura(
                idFactura,
                request.getMotivo(),
                request.getResponsable()
        );

        // 2. Devuelve la Nota de Crédito creada (HTTP 200 OK)
        return ResponseEntity.ok(notaCreditoGenerada);
    }
    // --- ⬆️ FIN: CÓDIGO NUEVO PARA HU 11 ⬆️ ---
}