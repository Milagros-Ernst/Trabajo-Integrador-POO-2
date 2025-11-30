package integrador.programa.repositorios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.enumeradores.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class FacturaRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FacturaRepositorio facturaRepositorio;

    private Cliente clienteTest;

    @BeforeEach
    void setUp() {
        // creación de cliente para las facturas
        clienteTest = Cliente.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("12345678")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Calle 123")
                .telefono("1234567890")
                .mail("juan@test.com")
                .direccionFiscal("Calle 123")
                .build();
        entityManager.persist(clienteTest);
        entityManager.flush();
    }

    @Test
    @DisplayName("Debe guardar y recuperar una factura")
    void debeGuardarYRecuperarFactura() {
        // arrange
        Factura factura = new Factura();
        factura.setPrecioTotal(1000.0);
        factura.setFecha(LocalDate.now());
        factura.setVencimiento(LocalDate.now().plusDays(30));
        factura.setEstado(EstadoFactura.VIGENTE);
        factura.setTipo(TipoComprobante.B);
        factura.setPeriodo(202511);
        factura.setEmpleadoResponsable("Admin");
        factura.setCliente(clienteTest);

        // act
        Factura guardada = facturaRepositorio.save(factura);

        // assert
        assertThat(guardada.getIdFactura()).isNotNull();
        assertThat(guardada.getPrecioTotal()).isEqualTo(1000.0);
        assertThat(guardada.getCliente().getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("Debe encontrar facturas por estado VIGENTE")
    void debeEncontrarFacturasPorEstadoVigente() {

        Factura factura1 = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.A);
        Factura factura2 = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.B);
        Factura factura3 = crearFactura(EstadoFactura.PAGADA, TipoComprobante.A);

        entityManager.persist(factura1);
        entityManager.persist(factura2);
        entityManager.persist(factura3);
        entityManager.flush();

        List<Factura> vigentes = facturaRepositorio.findByEstado(EstadoFactura.VIGENTE);

        assertThat(vigentes).hasSize(2);
        assertThat(vigentes).allMatch(f -> f.getEstado() == EstadoFactura.VIGENTE);
    }

    @Test
    @DisplayName("Debe encontrar facturas por estado PAGADA")
    void debeEncontrarFacturasPorEstadoPagada() {

        Factura factura1 = crearFactura(EstadoFactura.PAGADA, TipoComprobante.A);
        Factura factura2 = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.B);

        entityManager.persist(factura1);
        entityManager.persist(factura2);
        entityManager.flush();

        List<Factura> pagadas = facturaRepositorio.findByEstado(EstadoFactura.PAGADA);

        assertThat(pagadas).hasSize(1);
        assertThat(pagadas.get(0).getEstado()).isEqualTo(EstadoFactura.PAGADA);
    }

    @Test
    @DisplayName("Debe encontrar facturas por tipo A")
    void debeEncontrarFacturasPorTipoA() {

        Factura facturaA1 = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.A);
        Factura facturaA2 = crearFactura(EstadoFactura.PAGADA, TipoComprobante.A);
        Factura facturaB = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.B);

        entityManager.persist(facturaA1);
        entityManager.persist(facturaA2);
        entityManager.persist(facturaB);
        entityManager.flush();

        List<Factura> facturasA = facturaRepositorio.findByTipo(TipoComprobante.A);

        assertThat(facturasA).hasSize(2);
        assertThat(facturasA).allMatch(f -> f.getTipo() == TipoComprobante.A);
    }

    @Test
    @DisplayName("Debe encontrar facturas por tipo B")
    void debeEncontrarFacturasPorTipoB() {

        Factura facturaB1 = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.B);
        Factura facturaA = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.A);

        entityManager.persist(facturaB1);
        entityManager.persist(facturaA);
        entityManager.flush();

        List<Factura> facturasB = facturaRepositorio.findByTipo(TipoComprobante.B);

        assertThat(facturasB).hasSize(1);
        assertThat(facturasB.get(0).getTipo()).isEqualTo(TipoComprobante.B);
    }

    @Test
    @DisplayName("Debe encontrar factura por número de serie")
    void debeEncontrarFacturaPorNroSerie() {

        Factura factura = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.A);
        entityManager.persist(factura);
        entityManager.flush();

        Long nroSerie = factura.getNroSerie();

        Optional<Factura> encontrada = facturaRepositorio.findByNroSerie(nroSerie);

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNroSerie()).isEqualTo(nroSerie);
    }

    @Test
    @DisplayName("No debe encontrar factura con número de serie inexistente")
    void noDebeEncontrarFacturaConNroSerieInexistente() {

        Optional<Factura> encontrada = facturaRepositorio.findByNroSerie(99999L);
        assertThat(encontrada).isEmpty();
    }

    @Test
    @DisplayName("Debe encontrar facturas por cliente")
    void debeEncontrarFacturasPorCliente() {
        Factura factura1 = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.A);
        Factura factura2 = crearFactura(EstadoFactura.PAGADA, TipoComprobante.B);
        
        // crear otro cliente con su factura
        Cliente otroCliente = Cliente.builder()
                .nombre("María")
                .apellido("González")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("87654321")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.RESPONSABLE_INSCRIPTO)
                .direccion("Otra Calle 456")
                .telefono("9876543210")
                .mail("maria@test.com")
                .direccionFiscal("Otra Calle 456")
                .build();
        entityManager.persist(otroCliente);
        
        Factura facturaOtroCliente = crearFactura(EstadoFactura.VIGENTE, TipoComprobante.A);
        facturaOtroCliente.setCliente(otroCliente);

        entityManager.persist(factura1);
        entityManager.persist(factura2);
        entityManager.persist(facturaOtroCliente);
        entityManager.flush();

        List<Factura> facturasCliente = facturaRepositorio.findByCliente(clienteTest);

        assertThat(facturasCliente).hasSize(2);
        assertThat(facturasCliente).allMatch(f -> f.getCliente().getIdCuenta().equals(clienteTest.getIdCuenta()));
    }

    @Test
    @DisplayName("Debe retornar lista vacía si cliente no tiene facturas")
    void debeRetornarListaVaciaSiClienteSinFacturas() {

        Cliente clienteSinFacturas = Cliente.builder()
                .nombre("Pedro")
                .apellido("Ramírez")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("11111111")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Sin Facturas 789")
                .telefono("1111111111")
                .mail("pedro@test.com")
                .direccionFiscal("Sin Facturas 789")
                .build();
        entityManager.persist(clienteSinFacturas);
        entityManager.flush();

        List<Factura> facturas = facturaRepositorio.findByCliente(clienteSinFacturas);

        assertThat(facturas).isEmpty();
    }

    // metodo auxiliar para crear facturas
    private Factura crearFactura(EstadoFactura estado, TipoComprobante tipo) {
        Factura factura = new Factura();
        factura.setPrecioTotal(1000.0);
        factura.setFecha(LocalDate.now());
        factura.setVencimiento(LocalDate.now().plusDays(30));
        factura.setEstado(estado);
        factura.setTipo(tipo);
        factura.setPeriodo(202511);
        factura.setEmpleadoResponsable("Admin");
        factura.setCliente(clienteTest);
        return factura;
    }
}
