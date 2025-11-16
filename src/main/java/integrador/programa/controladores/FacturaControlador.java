package integrador.programa.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import integrador.programa.servicios.FacturaServicio;
import jakarta.validation.Valid;
import integrador.programa.modelo.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/facturas")
public class FacturaControlador {

    @Autowired
    private FacturaServicio facturaServicio;

    public FacturaControlador(FacturaServicio facturaServicio){
        this.facturaServicio = facturaServicio;
    }

    @GetMapping
    public ResponseEntity<List<Factura>> listar() {
        return ResponseEntity.ok(facturaServicio.listarFacturas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtener(@PathVariable String id) {
    return facturaServicio.buscarPorId(id)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody Factura factura) {
        Factura saved = facturaServicio.agregarFactura(factura);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<NotaCredito> eliminar(@PathVariable String id) {
        NotaCredito nota = facturaServicio.bajaFactura(id);
        if (nota == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(nota);
    }

}
