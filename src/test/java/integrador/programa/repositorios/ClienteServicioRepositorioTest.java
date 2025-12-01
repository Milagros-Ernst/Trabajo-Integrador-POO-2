package integrador.programa.repositorios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.ClienteServicio;
import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ClienteServicioRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteServicioRepositorio clienteServicioRepositorio;

    @Test
    void debeGuardarYRecuperarClienteServicio() {
        // Given
        Cliente cliente = crearCliente("Juan", "Perez", "20123456789");
        Servicio servicio = crearServicio("Internet", "Internet Fibra 100Mb", 5000.0);
        entityManager.persist(cliente);
        entityManager.persist(servicio);
        entityManager.flush();

        ClienteServicio cs = crearClienteServicio(cliente, servicio, EstadoServicio.ALTA);

        // When
        ClienteServicio guardado = clienteServicioRepositorio.save(cs);
        entityManager.flush();

        // Then
        assertThat(guardado.getIdClienteServicio()).isNotNull();
        assertThat(guardado.getCliente().getIdCuenta()).isEqualTo(cliente.getIdCuenta());
        assertThat(guardado.getServicio().getIdServicio()).isEqualTo(servicio.getIdServicio());
        assertThat(guardado.getEstadoServicio()).isEqualTo(EstadoServicio.ALTA);
    }

    @Test
    void debeEncontrarServiciosPorCliente() {

        Cliente cliente1 = crearCliente("Ana", "Garcia", "27456789012");
        Cliente cliente2 = crearCliente("Luis", "Martinez", "20987654321");
        Servicio internet = crearServicio("Internet", "Fibra optica", 4000.0);
        Servicio cable = crearServicio("Cable TV", "Television por cable", 3000.0);
        
        entityManager.persist(cliente1);
        entityManager.persist(cliente2);
        entityManager.persist(internet);
        entityManager.persist(cable);
        entityManager.flush();

        ClienteServicio cs1 = crearClienteServicio(cliente1, internet, EstadoServicio.ALTA);
        ClienteServicio cs2 = crearClienteServicio(cliente1, cable, EstadoServicio.ALTA);
        ClienteServicio cs3 = crearClienteServicio(cliente2, internet, EstadoServicio.ALTA);
        
        entityManager.persist(cs1);
        entityManager.persist(cs2);
        entityManager.persist(cs3);
        entityManager.flush();

        // When
        List<ClienteServicio> serviciosCliente1 = clienteServicioRepositorio.findByCliente(cliente1);

        // Then
        assertThat(serviciosCliente1).hasSize(2);
        assertThat(serviciosCliente1).extracting(cs -> cs.getServicio().getNombre())
                .containsExactlyInAnyOrder("Internet", "Cable TV");
    }

    @Test
    void debeEncontrarServiciosPorClienteYEstado() {
        // Given
        Cliente cliente = crearCliente("Pedro", "Lopez", "23111222333");
        Servicio internet = crearServicio("Internet", "Fibra", 5000.0);
        Servicio telefonia = crearServicio("Telefonia", "Linea fija", 1500.0);
        Servicio streaming = crearServicio("Streaming", "Video on demand", 2000.0);
        
        entityManager.persist(cliente);
        entityManager.persist(internet);
        entityManager.persist(telefonia);
        entityManager.persist(streaming);
        entityManager.flush();

        ClienteServicio cs1 = crearClienteServicio(cliente, internet, EstadoServicio.ALTA);
        ClienteServicio cs2 = crearClienteServicio(cliente, telefonia, EstadoServicio.BAJA);
        ClienteServicio cs3 = crearClienteServicio(cliente, streaming, EstadoServicio.ALTA);
        
        entityManager.persist(cs1);
        entityManager.persist(cs2);
        entityManager.persist(cs3);
        entityManager.flush();

        List<ClienteServicio> serviciosAlta = clienteServicioRepositorio.findByClienteAndEstadoServicio(
                cliente, EstadoServicio.ALTA);

        assertThat(serviciosAlta).hasSize(2);
        assertThat(serviciosAlta).extracting(cs -> cs.getServicio().getNombre())
                .containsExactlyInAnyOrder("Internet", "Streaming");
    }

    @Test
    void debeEncontrarServiciosDadosDeBaja() {

        Cliente cliente = crearCliente("Maria", "Fernandez", "27222333444");
        Servicio radio = crearServicio("Radio FM", "Radio digital", 800.0);
        Servicio antena = crearServicio("Antena", "TV satelital", 1200.0);
        
        entityManager.persist(cliente);
        entityManager.persist(radio);
        entityManager.persist(antena);
        entityManager.flush();

        ClienteServicio cs1 = crearClienteServicio(cliente, radio, EstadoServicio.BAJA);
        cs1.setFechaBaja(LocalDate.now().minusDays(30));
        
        ClienteServicio cs2 = crearClienteServicio(cliente, antena, EstadoServicio.BAJA);
        cs2.setFechaBaja(LocalDate.now().minusDays(15));
        
        entityManager.persist(cs1);
        entityManager.persist(cs2);
        entityManager.flush();

        List<ClienteServicio> serviciosBaja = clienteServicioRepositorio.findByClienteAndEstadoServicio(
                cliente, EstadoServicio.BAJA);

        assertThat(serviciosBaja).hasSize(2);
        assertThat(serviciosBaja).allMatch(cs -> cs.getFechaBaja() != null);
    }

    @Test
    void debeEncontrarRelacionClienteServicio() {

        Cliente cliente = crearCliente("Carlos", "Gomez", "20444555666");
        Servicio internet = crearServicio("Internet 200Mb", "Alta velocidad", 7000.0);
        
        entityManager.persist(cliente);
        entityManager.persist(internet);
        entityManager.flush();

        ClienteServicio cs = crearClienteServicio(cliente, internet, EstadoServicio.ALTA);
        entityManager.persist(cs);
        entityManager.flush();

        Optional<ClienteServicio> encontrado = clienteServicioRepositorio.findByClienteAndServicio(
                cliente, internet);

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getCliente().getIdCuenta()).isEqualTo(cliente.getIdCuenta());
        assertThat(encontrado.get().getServicio().getIdServicio()).isEqualTo(internet.getIdServicio());
    }

    @Test
    void noDebeEncontrarRelacionInexistente() {

        Cliente cliente = crearCliente("Roberto", "Silva", "23555666777");
        Servicio servicio = crearServicio("Cable Premium", "Canales HD", 6000.0);
        
        entityManager.persist(cliente);
        entityManager.persist(servicio);
        entityManager.flush();

        Optional<ClienteServicio> resultado = clienteServicioRepositorio.findByClienteAndServicio(
                cliente, servicio);

        assertThat(resultado).isEmpty();
    }

    @Test
    void debeRetornarListaVaciaSiClienteSinServicios() {

        Cliente cliente = crearCliente("Laura", "Diaz", "27555666777");
        entityManager.persist(cliente);
        entityManager.flush();

        List<ClienteServicio> servicios = clienteServicioRepositorio.findByCliente(cliente);

        assertThat(servicios).isEmpty();
    }

    @Test
    void debeValidarMetodoDarDeAlta() {

        Cliente cliente = crearCliente("Diego", "Torres", "20666777888");
        Servicio servicio = crearServicio("WiFi", "Conexion inalambrica", 3500.0);
        entityManager.persist(cliente);
        entityManager.persist(servicio);
        entityManager.flush();

        ClienteServicio cs = crearClienteServicio(cliente, servicio, EstadoServicio.BAJA);
        cs.setFechaBaja(LocalDate.now().minusDays(10));

        cs.darDeAlta(LocalDate.now());
        ClienteServicio guardado = clienteServicioRepositorio.save(cs);
        entityManager.flush();

        assertThat(guardado.getEstadoServicio()).isEqualTo(EstadoServicio.ALTA);
        assertThat(guardado.getFechaBaja()).isNull();
        assertThat(guardado.estaActivo()).isTrue();
    }

    @Test
    void debeValidarMetodoDarDeBaja() {
        Cliente cliente = crearCliente("Sofia", "Ramirez", "27777888999");
        Servicio servicio = crearServicio("Telefonia Movil", "Plan de celular", 2500.0);
        entityManager.persist(cliente);
        entityManager.persist(servicio);
        entityManager.flush();

        ClienteServicio cs = crearClienteServicio(cliente, servicio, EstadoServicio.ALTA);

        cs.darDeBaja(LocalDate.now());
        ClienteServicio guardado = clienteServicioRepositorio.save(cs);
        entityManager.flush();

        assertThat(guardado.getEstadoServicio()).isEqualTo(EstadoServicio.BAJA);
        assertThat(guardado.getFechaBaja()).isNotNull();
        assertThat(guardado.estaActivo()).isFalse();
    }

    @Test
    void debeValidarHistoricoDeServiciosDeCliente() {
        Cliente cliente = crearCliente("Gustavo", "Morales", "20888999000");
        Servicio s1 = crearServicio("Servicio 1", "Desc 1", 1000.0);
        Servicio s2 = crearServicio("Servicio 2", "Desc 2", 2000.0);
        Servicio s3 = crearServicio("Servicio 3", "Desc 3", 3000.0);
        
        entityManager.persist(cliente);
        entityManager.persist(s1);
        entityManager.persist(s2);
        entityManager.persist(s3);
        entityManager.flush();

        ClienteServicio cs1 = crearClienteServicio(cliente, s1, EstadoServicio.ALTA);
        ClienteServicio cs2 = crearClienteServicio(cliente, s2, EstadoServicio.BAJA);
        cs2.setFechaBaja(LocalDate.now().minusMonths(6));
        ClienteServicio cs3 = crearClienteServicio(cliente, s3, EstadoServicio.ALTA);
        
        entityManager.persist(cs1);
        entityManager.persist(cs2);
        entityManager.persist(cs3);
        entityManager.flush();

        List<ClienteServicio> historico = clienteServicioRepositorio.findByCliente(cliente);

        assertThat(historico).hasSize(3);
        assertThat(historico.stream().filter(cs -> cs.getEstadoServicio() == EstadoServicio.ALTA).count()).isEqualTo(2);
        assertThat(historico.stream().filter(cs -> cs.getEstadoServicio() == EstadoServicio.BAJA).count()).isEqualTo(1);
    }

    // metodos auxiliares
    private Cliente crearCliente(String nombre, String apellido, String numeroDocumento) {
        Cliente cliente = new Cliente();
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setTipoDocumento(TipoDocumento.CUIT);
        cliente.setNumeroDocumento(numeroDocumento);
        cliente.setDireccion("Calle 123");
        cliente.setDireccionFiscal("Calle 123");
        cliente.setTelefono("1234567890");
        cliente.setMail(nombre.toLowerCase() + "@test.com");
        cliente.setCondIVA(CondicionIVA.RESPONSABLE_INSCRIPTO);
        cliente.setEstadoCuenta(EstadoCuenta.ACTIVA);
        return cliente;
    }

    private Servicio crearServicio(String nombre, String descripcion, Double precio) {
        return new Servicio(nombre, descripcion, precio, TipoIVA.IVA_21, EstadoServicio.ALTA);
    }

    private ClienteServicio crearClienteServicio(Cliente cliente, Servicio servicio, EstadoServicio estado) {
        ClienteServicio cs = new ClienteServicio();
        cs.setCliente(cliente);
        cs.setServicio(servicio);
        cs.setEstadoServicio(estado);
        cs.setFechaAlta(LocalDate.now());
        return cs;
    }
}
