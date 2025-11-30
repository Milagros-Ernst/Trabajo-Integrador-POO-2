package integrador.programa.repositorios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Factura;
import integrador.programa.modelo.NotaCredito;
import integrador.programa.modelo.enumeradores.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotaRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotaRepositorio notaRepositorio;

    @Test
    void debeGuardarYRecuperarNotaCredito() {
        // given
        Cliente cliente = crearCliente("Juan", "Perez", "20123456789");
        entityManager.persist(cliente);

        Factura factura = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.A);
        entityManager.persist(factura);
        entityManager.flush();

        NotaCredito nota = new NotaCredito();
        nota.setPrecioTotal(5000.0);
        nota.setTipo(TipoComprobante.A);
        nota.setFecha(LocalDate.now());
        nota.setEmpleadoResponsable("Maria Lopez");
        nota.setMotivoAnulacion("Error en facturación");
        nota.setFacturaAnulada(factura);

        // when
        NotaCredito guardada = notaRepositorio.save(nota);
        entityManager.flush();

        // then
        assertThat(guardada.getNroNota()).isNotNull();
        assertThat(guardada.getPrecioTotal()).isEqualTo(5000.0);
        assertThat(guardada.getMotivoAnulacion()).isEqualTo("Error en facturación");
        assertThat(guardada.getFacturaAnulada().getIdFactura()).isEqualTo(factura.getIdFactura());
    }

    @Test
    void debeEncontrarNotasPorIdFacturaAnulada() {
        // given
        Cliente cliente = crearCliente("Ana", "Garcia", "27456789012");
        entityManager.persist(cliente);

        Factura factura1 = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.A);
        Factura factura2 = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.B);
        Factura factura3 = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.A);
        entityManager.persist(factura1);
        entityManager.persist(factura2);
        entityManager.persist(factura3);
        entityManager.flush();

        NotaCredito nota1 = crearNotaCredito(factura1, 3000.0, "Duplicación de factura");
        NotaCredito nota2 = crearNotaCredito(factura2, 1500.0, "Corrección de monto");
        NotaCredito nota3 = crearNotaCredito(factura3, 2000.0, "Error administrativo");
        
        entityManager.persist(nota1);
        entityManager.persist(nota2);
        entityManager.persist(nota3);
        entityManager.flush();

        // when
        List<NotaCredito> notasFactura1 = notaRepositorio.findByFacturaAnulada_IdFactura(factura1.getIdFactura());

        // then
        assertThat(notasFactura1).hasSize(1);
        assertThat(notasFactura1.get(0).getPrecioTotal()).isEqualTo(3000.0);
    }

    @Test
    void debeRetornarListaVaciaSiFacturaSinNotas() {
        // given
        Cliente cliente = crearCliente("Pedro", "Lopez", "23111222333");
        entityManager.persist(cliente);

        Factura factura = crearFactura(cliente, EstadoFactura.VIGENTE, TipoComprobante.C);
        entityManager.persist(factura);
        entityManager.flush();

        // when
        List<NotaCredito> notas = notaRepositorio.findByFacturaAnulada_IdFactura(factura.getIdFactura());

        // then
        assertThat(notas).isEmpty();
    }

    @Test
    void debeListarTodasLasNotas() {
        Cliente cliente = crearCliente("Luis", "Martinez", "20987654321");
        entityManager.persist(cliente);

        Factura factura1 = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.A);
        Factura factura2 = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.B);
        Factura factura3 = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.C);
        entityManager.persist(factura1);
        entityManager.persist(factura2);
        entityManager.persist(factura3);
        entityManager.flush();

        NotaCredito nota1 = crearNotaCredito(factura1, 1000.0, "Motivo 1");
        NotaCredito nota2 = crearNotaCredito(factura2, 2000.0, "Motivo 2");
        NotaCredito nota3 = crearNotaCredito(factura3, 3000.0, "Motivo 3");
        
        entityManager.persist(nota1);
        entityManager.persist(nota2);
        entityManager.persist(nota3);
        entityManager.flush();

        List<NotaCredito> notas = notaRepositorio.findAll();

        assertThat(notas).hasSize(3);
    }

    @Test
    void debeValidarDiferentesTiposComprobante() {
        Cliente cliente = crearCliente("Maria", "Fernandez", "27222333444");
        entityManager.persist(cliente);

        Factura facturaA = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.A);
        Factura facturaB = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.B);
        Factura facturaC = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.C);
        entityManager.persist(facturaA);
        entityManager.persist(facturaB);
        entityManager.persist(facturaC);
        entityManager.flush();

        NotaCredito notaA = crearNotaCredito(facturaA, 1000.0, "Nota tipo A");
        notaA.setTipo(TipoComprobante.A);
        
        NotaCredito notaB = crearNotaCredito(facturaB, 2000.0, "Nota tipo B");
        notaB.setTipo(TipoComprobante.B);
        
        NotaCredito notaC = crearNotaCredito(facturaC, 3000.0, "Nota tipo C");
        notaC.setTipo(TipoComprobante.C);
        
        entityManager.persist(notaA);
        entityManager.persist(notaB);
        entityManager.persist(notaC);
        entityManager.flush();

        List<NotaCredito> notas = notaRepositorio.findAll();

        assertThat(notas).hasSize(3);
        assertThat(notas).extracting(NotaCredito::getTipo)
                .containsExactlyInAnyOrder(TipoComprobante.A, TipoComprobante.B, TipoComprobante.C);
    }

    @Test
    void debeValidarEmpleadoResponsable() {
        Cliente cliente = crearCliente("Carlos", "Gomez", "20444555666");
        entityManager.persist(cliente);

        Factura factura = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.A);
        entityManager.persist(factura);
        entityManager.flush();

        NotaCredito nota = crearNotaCredito(factura, 4500.0, "Corrección solicitada por cliente");
        nota.setEmpleadoResponsable("Roberto Silva");

        NotaCredito guardada = notaRepositorio.save(nota);
        entityManager.flush();

        assertThat(guardada.getEmpleadoResponsable()).isEqualTo("Roberto Silva");
    }

    @Test
    void debeValidarFechaEmision() {
        Cliente cliente = crearCliente("Laura", "Diaz", "27555666777");
        entityManager.persist(cliente);

        Factura factura = crearFactura(cliente, EstadoFactura.ANULADA, TipoComprobante.B);
        entityManager.persist(factura);
        entityManager.flush();

        NotaCredito nota = crearNotaCredito(factura, 3500.0, "Anulación parcial");

        NotaCredito guardada = notaRepositorio.save(nota);
        entityManager.flush();

        assertThat(guardada.getFecha()).isNotNull();
        assertThat(guardada.getFecha()).isEqualTo(LocalDate.now());
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

    private Factura crearFactura(Cliente cliente, EstadoFactura estado, TipoComprobante tipo) {
        Factura factura = new Factura();
        factura.setCliente(cliente);
        factura.setEstado(estado);
        factura.setTipo(tipo);
        factura.setFecha(LocalDate.now());
        factura.setEmpleadoResponsable("Admin");
        factura.setPrecioTotal(10000.0);
        factura.setPeriodo(202411);
        factura.setVencimiento(LocalDate.now().plusDays(30));
        return factura;
    }

    private NotaCredito crearNotaCredito(Factura factura, Double precio, String motivo) {
        NotaCredito nota = new NotaCredito();
        nota.setFacturaAnulada(factura);
        nota.setPrecioTotal(precio);
        nota.setTipo(factura.getTipo());
        nota.setFecha(LocalDate.now());
        nota.setEmpleadoResponsable("Admin");
        nota.setMotivoAnulacion(motivo);
        return nota;
    }
}
