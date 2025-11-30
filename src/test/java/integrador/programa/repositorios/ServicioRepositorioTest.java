package integrador.programa.repositorios;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ServicioRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ServicioRepositorio servicioRepositorio;

    @Test
    void debeGuardarYRecuperarServicio() {
        // given
        Servicio servicio = crearServicio("Internet Fibra óptica", "Internet de alta velocidad", 5000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);

        // when
        Servicio guardado = servicioRepositorio.save(servicio);
        entityManager.flush();

        // then
        assertThat(guardado.getIdServicio()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Internet Fibra óptica");
        assertThat(guardado.getEstadoServicio()).isEqualTo(EstadoServicio.ALTA);
    }

    @Test
    void debeEncontrarServiciosPorEstadoAlta() {
        // given
        Servicio servicio1 = crearServicio("Cable TV", "Television por cable", 3000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        Servicio servicio2 = crearServicio("Telefonia", "Linea telefonica fija", 1500.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        Servicio servicio3 = crearServicio("Streaming", "Servicio de streaming", 2000.0, TipoIVA.IVA_21, EstadoServicio.BAJA);
        
        entityManager.persist(servicio1);
        entityManager.persist(servicio2);
        entityManager.persist(servicio3);
        entityManager.flush();

        // when
        List<Servicio> serviciosAlta = servicioRepositorio.findByEstadoServicio(EstadoServicio.ALTA);

        // then
        assertThat(serviciosAlta).hasSize(2);
        assertThat(serviciosAlta).extracting(Servicio::getNombre)
                .containsExactlyInAnyOrder("Cable TV", "Telefonia");
    }

    @Test
    void debeEncontrarServiciosPorEstadoBaja() {
        Servicio servicio1 = crearServicio("Radio FM", "Servicio de radio", 500.0, TipoIVA.IVA_21, EstadoServicio.BAJA);
        Servicio servicio2 = crearServicio("WiFi Premium", "Internet premium", 7000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        Servicio servicio3 = crearServicio("Antena", "Antena parabolica", 800.0, TipoIVA.IVA_21, EstadoServicio.BAJA);
        
        entityManager.persist(servicio1);
        entityManager.persist(servicio2);
        entityManager.persist(servicio3);
        entityManager.flush();

        List<Servicio> serviciosBaja = servicioRepositorio.findByEstadoServicio(EstadoServicio.BAJA);

        assertThat(serviciosBaja).hasSize(2);
        assertThat(serviciosBaja).extracting(Servicio::getNombre)
                .containsExactlyInAnyOrder("Radio FM", "Antena");
    }

    @Test
    void debeRetornarListaVaciaSiNoHayServiciosConEstado() {
        Servicio servicio = crearServicio("Mantenimiento", "Servicio de mantenimiento", 2500.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        entityManager.persist(servicio);
        entityManager.flush();

        List<Servicio> serviciosBaja = servicioRepositorio.findByEstadoServicio(EstadoServicio.BAJA);

        assertThat(serviciosBaja).isEmpty();
    }

    @Test
    void debeListarTodosLosServicios() {
        Servicio servicio1 = crearServicio("Servicio A", "Descripcion A", 1000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        Servicio servicio2 = crearServicio("Servicio B", "Descripcion B", 2000.0, TipoIVA.IVA_105, EstadoServicio.BAJA);
        Servicio servicio3 = crearServicio("Servicio C", "Descripcion C", 3000.0, TipoIVA.IVA_27, EstadoServicio.ALTA);
        
        entityManager.persist(servicio1);
        entityManager.persist(servicio2);
        entityManager.persist(servicio3);
        entityManager.flush();

        List<Servicio> servicios = servicioRepositorio.findAll();

        assertThat(servicios).hasSize(3);
    }

    @Test
    void debeValidarDiferentesTiposIVA() {
        Servicio servicio1 = crearServicio("IVA 21%", "Servicio con IVA 21", 1000.0, TipoIVA.IVA_21, EstadoServicio.ALTA);
        Servicio servicio2 = crearServicio("IVA 10.5%", "Servicio con IVA 10.5", 2000.0, TipoIVA.IVA_105, EstadoServicio.ALTA);
        Servicio servicio3 = crearServicio("IVA 27%", "Servicio con IVA 27", 3000.0, TipoIVA.IVA_27, EstadoServicio.ALTA);
        
        entityManager.persist(servicio1);
        entityManager.persist(servicio2);
        entityManager.persist(servicio3);
        entityManager.flush();

        List<Servicio> servicios = servicioRepositorio.findAll();

        assertThat(servicios).hasSize(3);
        assertThat(servicios).extracting(Servicio::getTipoIva)
                .containsExactlyInAnyOrder(TipoIVA.IVA_21, TipoIVA.IVA_105, TipoIVA.IVA_27);
    }

    @Test
    void debeGuardarServicioConEstadoPorDefectoAlta() {
        Servicio servicio = new Servicio();
        servicio.setNombre("Servicio Default");
        servicio.setDescripcion("Descripcion del servicio con estado por defecto");
        servicio.setPrecioUnitario(1500.0);
        servicio.setTipoIva(TipoIVA.IVA_21);

        Servicio guardado = servicioRepositorio.save(servicio);
        entityManager.flush();

        assertThat(guardado.getEstadoServicio()).isEqualTo(EstadoServicio.ALTA);
    }

    // metodo auxiliar
    private Servicio crearServicio(String nombre, String descripcion, Double precio, TipoIVA tipoIva, EstadoServicio estado) {
        return new Servicio(nombre, descripcion, precio, tipoIva, estado);
    }
}
