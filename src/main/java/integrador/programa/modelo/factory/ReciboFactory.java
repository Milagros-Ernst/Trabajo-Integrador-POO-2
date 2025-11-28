package integrador.programa.modelo.factory;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.DetalleRecibo;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.Pago;
import integrador.programa.modelo.Recibo;

public class ReciboFactory {

    // Crea un Recibo a partir de un único pago sobre una factura.
    public static Recibo crearDesdePago(Pago pago) {
        if (pago == null || pago.getFactura() == null) {
            throw new IllegalArgumentException("El pago y su factura no pueden ser nulos para emitir un recibo");
        }

        Factura factura = pago.getFactura();
        Cliente cliente = factura.getCliente();

        // Crear el recibo y asociarlo al cliente
        Recibo recibo = new Recibo();
        recibo.setCliente(cliente);

        // Vincular el pago al recibo (setea pago.setRecibo(this))
        recibo.agregarPago(pago);

        // Calcular saldo pendiente de la factura DESPUÉS de este pago
        double saldoPendiente = factura.calcularSaldoPendiente();

        // Crear el detalle del recibo
        DetalleRecibo detalle = new DetalleRecibo();
        detalle.setFactura(factura);
        detalle.setImporteAplicado(pago.getImporte());
        detalle.setSaldoPendienteFactura(saldoPendiente);

        // Agregar detalle al recibo (recalcula importe_total)
        recibo.agregarDetalle(detalle);

        return recibo;
    }
}
