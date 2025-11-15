package integrador.programa.servicios;

import integrador.programa.modelo.Cliente; // Asumo que existe
import integrador.programa.modelo.Factura;
import integrador.programa.repositorios.CuentaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaService {
    
    @Autowired
    private CuentaRepository cuentaRepository;

    /**
     * (AC 5)
     * Revierte el impacto de una factura en el saldo de la cuenta
     * de un cliente.
     * Esta lógica asume que la Cuenta tiene un 'saldo' que representa
     * la deuda del cliente (un número positivo es deuda).
     * @param factura La factura que está siendo anulada.
     */
    @Transactional
    public void revertirImpactoFacturaEnSaldo(Factura factura) {
        
        // 1. Validar Cliente
        // (Asumo que Factura tiene getCliente() gracias a Lombok)
        Cliente cliente = factura.getCliente(); 
        if (cliente == null) {
            // (Asumo que Factura tiene getIdFactura() gracias a Lombok)
            System.err.println("Advertencia: Factura " + factura.getIdFactura() + " no tiene cliente asociado.");
            return;
        }

        // acá había código que tomaba que cliente no es cuenta, pero
        // cliente ES cuenta entonces esa parte medio que causaba conflictos wa
        double montoARevertir = factura.getPrecioTotal(); 
        
        // 4. Revertir Saldo
        // Asumiendo que un saldo positivo es DEUDA:
        // Al anular la factura, la deuda del cliente disminuye.
        
        // como ya dije cliente es una Cuenta, así que podríamos guardar directamente
        cuentaRepository.save(cliente);

    }
    
    // ... otros métodos de CuentaService
    
} // <-- Llave de cierre de la clase que faltaba