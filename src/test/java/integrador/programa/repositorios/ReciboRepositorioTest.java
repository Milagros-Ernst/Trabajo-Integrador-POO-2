package integrador.programa.repositorios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Recibo;
import integrador.programa.modelo.enumeradores.CondicionIVA;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ReciboRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReciboRepositorio reciboRepositorio;

    @Test
    void debeGuardarYRecuperarRecibo() {
        // given
        Cliente cliente = crearCliente("Juan", "Perez", "20123456789");
        entityManager.persist(cliente);
        entityManager.flush();

        Recibo recibo = Recibo.builder()
                .nroRecibo(1001L)
                .fechaEmision(LocalDate.now())
                .cliente(cliente)
                .importeTotal(5000.0)
                .build();

        // when
        Recibo guardado = reciboRepositorio.save(recibo);
        entityManager.flush();

        // then
        assertThat(guardado.getIdRecibo()).isNotNull();
        assertThat(guardado.getNroRecibo()).isEqualTo(1001L);
        assertThat(guardado.getCliente().getIdCuenta()).isEqualTo(cliente.getIdCuenta());
        assertThat(guardado.getImporteTotal()).isEqualTo(5000.0);
    }

    @Test
    void debeEncontrarRecibosPorCliente() {
        Cliente cliente1 = crearCliente("Ana", "Garcia", "27456789012");
        Cliente cliente2 = crearCliente("Luis", "Martinez", "20987654321");
        entityManager.persist(cliente1);
        entityManager.persist(cliente2);

        Recibo recibo1 = crearRecibo(2001L, cliente1, 3000.0);
        Recibo recibo2 = crearRecibo(2002L, cliente1, 4000.0);
        Recibo recibo3 = crearRecibo(2003L, cliente2, 5000.0);
        
        entityManager.persist(recibo1);
        entityManager.persist(recibo2);
        entityManager.persist(recibo3);
        entityManager.flush();

        List<Recibo> recibosCliente1 = reciboRepositorio.findByCliente(cliente1);

        assertThat(recibosCliente1).hasSize(2);
        assertThat(recibosCliente1).extracting(Recibo::getNroRecibo)
                .containsExactlyInAnyOrder(2001L, 2002L);
    }

    @Test
    void debeRetornarListaVaciaSiClienteSinRecibos() {
        Cliente cliente = crearCliente("Pedro", "Lopez", "23111222333");
        entityManager.persist(cliente);
        entityManager.flush();

        List<Recibo> recibos = reciboRepositorio.findByCliente(cliente);


        assertThat(recibos).isEmpty();
    }

    @Test
    void debeEncontrarReciboPorNroRecibo() {

        Cliente cliente = crearCliente("Maria", "Fernandez", "27222333444");
        entityManager.persist(cliente);

        Recibo recibo = crearRecibo(3001L, cliente, 2500.0);
        entityManager.persist(recibo);
        entityManager.flush();

        Recibo encontrado = reciboRepositorio.findByNroRecibo(3001L);

        assertThat(encontrado).isNotNull();
        assertThat(encontrado.getNroRecibo()).isEqualTo(3001L);
        assertThat(encontrado.getImporteTotal()).isEqualTo(2500.0);
    }

    @Test
    void noDebeEncontrarReciboConNroReciboInexistente() {
        Recibo recibo = reciboRepositorio.findByNroRecibo(99999L);

        assertThat(recibo).isNull();
    }

    @Test
    void debeObtenerUltimoNumeroReciboConRecibosExistentes() {
        Cliente cliente = crearCliente("Carlos", "Gomez", "20444555666");
        entityManager.persist(cliente);

        Recibo recibo1 = crearRecibo(1000L, cliente, 1000.0);
        Recibo recibo2 = crearRecibo(2000L, cliente, 2000.0);
        Recibo recibo3 = crearRecibo(1500L, cliente, 1500.0);
        
        entityManager.persist(recibo1);
        entityManager.persist(recibo2);
        entityManager.persist(recibo3);
        entityManager.flush();

        Long ultimoNumero = reciboRepositorio.obtenerUltimoNumero();

        assertThat(ultimoNumero).isEqualTo(2000L);
    }

    @Test
    void debeRetornarCeroCuandoNoHayRecibos() {
        Long ultimoNumero = reciboRepositorio.obtenerUltimoNumero();

        assertThat(ultimoNumero).isEqualTo(0L);
    }

    @Test
    void debeContarRecibosPorCliente() {
        Cliente cliente = crearCliente("Roberto", "Silva", "23555666777");
        entityManager.persist(cliente);

        Recibo recibo1 = crearRecibo(4001L, cliente, 1000.0);
        Recibo recibo2 = crearRecibo(4002L, cliente, 2000.0);
        Recibo recibo3 = crearRecibo(4003L, cliente, 3000.0);
        
        entityManager.persist(recibo1);
        entityManager.persist(recibo2);
        entityManager.persist(recibo3);
        entityManager.flush();

        List<Recibo> recibos = reciboRepositorio.findByCliente(cliente);

        assertThat(recibos).hasSize(3);
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

    private Recibo crearRecibo(Long nroRecibo, Cliente cliente, Double importe) {
        return Recibo.builder()
                .nroRecibo(nroRecibo)
                .fechaEmision(LocalDate.now())
                .cliente(cliente)
                .importeTotal(importe)
                .build();
    }
}
