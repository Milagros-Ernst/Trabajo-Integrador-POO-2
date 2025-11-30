package integrador.programa.modelo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;

public class ServicioTest {

    @Test
    @DisplayName("Un servicio nuevo debe iniciar en estado ALTA (baja lógica por estado)")
    void testEstadoPorDefectoEsAlta() {
        Servicio servicio = new Servicio();

        assertEquals(EstadoServicio.ALTA, servicio.getEstadoServicio(),
                "Al crear un servicio sin parámetros el estado debe ser ALTA");
    }

    @Test
    @DisplayName("El constructor completo debe inicializar correctamente los atributos")
    void testConstructorCompletoInicializaCampos() {
        String nombre = "Internet 100MB";
        String descripcion = "Servicio de internet fibra óptica 100MB";
        Double precio = 5000.0;
        TipoIVA tipoIva = TipoIVA.IVA_21;
        EstadoServicio estado = EstadoServicio.ALTA;

        Servicio servicio = new Servicio(nombre, descripcion, precio, tipoIva, estado);

        assertEquals(nombre, servicio.getNombre());
        assertEquals(descripcion, servicio.getDescripcion());
        assertEquals(precio, servicio.getPrecioUnitario());
        assertEquals(tipoIva, servicio.getTipoIva());
        assertEquals(estado, servicio.getEstadoServicio());
    }

    @Test
    @DisplayName("Debe permitir modificar los datos del servicio respetando las reglas de negocio")
    void testModificarDatosServicio() {
        Servicio servicio = new Servicio();
        servicio.setNombre("Internet 50MB");
        servicio.setDescripcion("Servicio inicial");
        servicio.setPrecioUnitario(3000.0);
        servicio.setTipoIva(TipoIVA.IVA_105);
        servicio.setEstadoServicio(EstadoServicio.ALTA);

        // Modificación de datos (HU 05)
        servicio.setNombre("Internet 100MB");
        servicio.setDescripcion("Servicio actualizado a 100MB");
        servicio.setPrecioUnitario(4500.0);
        servicio.setTipoIva(TipoIVA.IVA_21);
        servicio.setEstadoServicio(EstadoServicio.BAJA); // simulando baja lógica (HU 06)

        assertEquals("Internet 100MB", servicio.getNombre());
        assertEquals("Servicio actualizado a 100MB", servicio.getDescripcion());
        assertEquals(4500.0, servicio.getPrecioUnitario());
        assertEquals(TipoIVA.IVA_21, servicio.getTipoIva());
        assertEquals(EstadoServicio.BAJA, servicio.getEstadoServicio());
    }

    @Test
    @DisplayName("La baja lógica del servicio se representa con estado BAJA")
    void testBajaLogicaServicio() {
        Servicio servicio = new Servicio();
        assertEquals(EstadoServicio.ALTA, servicio.getEstadoServicio(),
                "Al inicio debe estar en ALTA");

        // Simulamos acción de 'Desactivar' (HU 06)
        servicio.setEstadoServicio(EstadoServicio.BAJA);

        assertEquals(EstadoServicio.BAJA, servicio.getEstadoServicio(),
                "Al desactivar el servicio debe quedar en BAJA");
    }
}
