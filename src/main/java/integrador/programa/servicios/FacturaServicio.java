package integrador.programa.servicios;

import integrador.programa.modelo.Factura;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.enumeradores.EstadoFactura;
// Importamos tu Repositorio
import integrador.programa.repositorios.FacturaRepositorio; 
import integrador.programa.repositorios.NotaCreditoRepository;
import integrador.programa.servicios.excepciones.FacturaNoAnulableException;
import integrador.programa.servicios.excepciones.FacturaNoEncontradaException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // MUY IMPORTANTE

import java.time.LocalDate;

@Service
public class FacturaServicio {

    @Autowired
    // Usamos el nombre de tu repositorio: FacturaRepositorio
    private FacturaRepositorio facturaRepositorio; 

    @Autowired
    private NotaCreditoRepository notaCreditoRepository;
    
    // (AC 5) Inyectamos el servicio de Cuentas para afectar el saldo
    @Autowired
    private CuentaService cuentaService; 

    /**
     * Implementa la lógica de negocio de la HU 11.
     * Anula una factura vigente, crea la Nota de Crédito asociada
     * y revierte el saldo en la cuenta del cliente.
     *
     * @param idFactura ID (String UUID) de la factura a anular.
     * @param motivo (AC 2) Motivo de la anulación.
     * @param responsable (AC 2) Usuario que realiza la operación.
     * @return La NotaCredito generada.
     */
    @Transactional // Asegura que toda la operación sea atómica (o todo o nada)
    public NotaCredito anularFactura(String idFactura, String motivo, String responsable) {

        // 1. Buscar la factura por su ID (String)
        Factura factura = facturaRepositorio.findById(idFactura)
                .orElseThrow(() -> new FacturaNoEncontradaException("No se encontró la factura con ID: " + idFactura));

        // 2. VALIDAR ESTADO (AC 1 y AC 4)
        // Usamos tu enum VIGENTE como "emitida"
        if (factura.getEstado() != EstadoFactura.VIGENTE) {
            throw new FacturaNoAnulableException(
                "La factura (ID: " + idFactura + ") no puede anularse. Estado actual: " + factura.getEstado());
        }

        // 3. CREAR NOTA DE CRÉDITO (AC 3)
        NotaCredito notaCredito = new NotaCredito(
                LocalDate.now(),
                motivo,
                responsable,
                factura.getPrecioTotal(), // Usamos el campo de tu clase Factura
                factura                   // Vínculo a la factura original
        );
        
        // 4. ACTUALIZAR FACTURA (AC 4)
        factura.setEstado(EstadoFactura.ANULADA);
        
        // (AC 6) Asignamos el vínculo bidireccional
        factura.setNotaCreditoAnulacion(notaCredito); 

        // 5. AFECTAR SALDO CLIENTE (AC 5)
        // Delegamos esta lógica al servicio de Cuentas
        cuentaService.revertirImpactoFacturaEnSaldo(factura);

        // 6. PERSISTIR CAMBIOS
        // Guardamos la nueva Nota de Crédito
        NotaCredito notaCreditoGuardada = notaCreditoRepository.save(notaCredito);
        
        // Actualizamos la Factura (que ahora está ANULADA y vinculada a la NC)
        facturaRepositorio.save(factura);

        // 7. Retornar el documento generado
        return notaCreditoGuardada;
    }

    // ... otros métodos del servicio (crearFactura, buscarFactura, etc.)
}