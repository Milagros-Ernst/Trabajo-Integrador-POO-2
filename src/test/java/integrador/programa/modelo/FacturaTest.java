package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.CondicionIVA;
import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.TipoIVA;

public class FacturaTest {

    private Factura factura;
    private Cliente cliente;

    // ejecutado antes de cada @Test, esta bueno
    @BeforeEach
    public void inicio() {
        // Preparamos una factura vacía y un cliente básico
        factura = new Factura();
        
        cliente = new Cliente();
        cliente.setNombre("Juan");
        cliente.setApellido("Perez");
        cliente.setCondIVA(CondicionIVA.CONSUMIDOR_FINAL);
        
        // datos básicos
        factura.setCliente(cliente);
        factura.setEstado(EstadoFactura.VIGENTE);
        factura.setDetalles(new ArrayList<>()); // Inicializamos la lista
    }

    @Test
    @DisplayName("Debe asignar y obtener el cliente correctamente")
    void testAsignarCliente() {
        // verificamos que esté vinculado
        Cliente clienteActual = factura.getCliente();

        assertNotNull(clienteActual, "El cliente no debería ser nulo");
        assertEquals("Juan", clienteActual.getNombre());
        assertEquals(CondicionIVA.CONSUMIDOR_FINAL, clienteActual.getCondIVA());
    }

    @Test
    @DisplayName("Debe calcular el precio total correctamente con un solo servicio")
    void testCalcularTotalUnServicio() {
        // crear Servicio y Detalle
        Servicio servicio = new Servicio();
        servicio.setNombre("Limpieza");
        servicio.setPrecioUnitario(100.0);
        servicio.setTipoIva(TipoIVA.IVA_21);

        DetalleFactura detalle = new DetalleFactura();
        detalle.setServicio(servicio);
        detalle.setPrecio(100); 
        detalle.setFactura(factura);

        factura.agregarDetalle(detalle);
        factura.calcularTotal(); 

        assertEquals(121.0, factura.getPrecioTotal(), 0.001, "El total debería ser 121.0 (100 + IVA 21%)");
    }

    @Test
    @DisplayName("Debe calcular el precio total acumulado con múltiples servicios")
    void testCalcularTotalMultiplesServicios() {
        // Servicio 1: $100 + 21% IVA = $121
        Servicio s1 = new Servicio();
        s1.setPrecioUnitario(100.0);
        s1.setTipoIva(TipoIVA.IVA_21);
        
        DetalleFactura d1 = new DetalleFactura();
        d1.setServicio(s1);
        d1.setPrecio(100);

        // Servicio 2: $200 + 10.5% IVA = $221
        Servicio s2 = new Servicio();
        s2.setPrecioUnitario(200.0);
        s2.setTipoIva(TipoIVA.IVA_105);

        DetalleFactura d2 = new DetalleFactura();
        d2.setServicio(s2);
        d2.setPrecio(200);

        factura.agregarDetalle(d1);
        factura.agregarDetalle(d2);
        factura.calcularTotal();

        // vemos si cumple
        assertEquals(342.0, factura.getPrecioTotal(), 0.001);
    }

    @Test
    @DisplayName("Debe agregar detalles")
    void testGestionarDetalles() {
        DetalleFactura detalle = new DetalleFactura();
        detalle.setDescripcion("Test Detalle");
        
        factura.agregarDetalle(detalle);
        
        assertEquals(1, factura.getDetalles().size(), "Debería haber 1 detalle");
        assertTrue(factura.getDetalles().contains(detalle));
    }

}