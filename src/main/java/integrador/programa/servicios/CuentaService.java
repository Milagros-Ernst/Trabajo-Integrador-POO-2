package integrador.programa.servicios;

import integrador.programa.modelo.Cliente; // Asumo que existe
import integrador.programa.modelo.Cuenta;   // Asumo que existe (Línea corregida)
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

        // 2. Validar Cuenta
        // (Asumo que Cliente tiene getCuenta())
        Cuenta cuenta = cliente.getCuenta(); 
        if (cuenta == null) {
            // (Asumo que Cliente tiene getId())
            System.err.println("Advertencia: Cliente " + cliente.getId() + " no tiene cuenta asociada.");
            return;
        }

        // --- INICIO DE CÓDIGO FALTANTE ---

        // 3. Obtener Monto
        // (Asumo que Factura tiene getPrecioTotal() gracias a Lombok)
        double montoARevertir = factura.getPrecioTotal(); 
        
        // 4. Revertir Saldo
        // Asumiendo que un saldo positivo es DEUDA:
        // Al anular la factura, la deuda del cliente disminuye.
        
        // (Asumo que Cuenta tiene getSaldo())
        double saldoActual = cuenta.getSaldo(); 
        
        // (Asumo que Cuenta tiene setSaldo())
        cuenta.setSaldo(saldoActual - montoARevertir); 
        
        // 5. Guardar
        cuentaRepository.save(cuenta);

        // --- FIN DE CÓDIGO FALTANTE ---
    }
    
    // ... otros métodos de CuentaService
    
} // <-- Llave de cierre de la clase que faltaba