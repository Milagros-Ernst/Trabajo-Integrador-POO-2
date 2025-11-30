package integrador.programa.repositorios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import integrador.programa.modelo.enumeradores.CondicionIVA;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ClienteRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ClienteRepositorio clienteRepositorio;

    @Test
    @DisplayName("Debe guardar y recuperar un cliente")
    void deberiaGuardarYRecuperarCliente() {
        // Arrange
        Cliente cliente = Cliente.builder()
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

        Cliente guardado = clienteRepositorio.save(cliente);

        assertThat(guardado.getIdCuenta()).isNotNull();
        assertThat(guardado.getNombre()).isEqualTo("Juan");
        assertThat(guardado.getApellido()).isEqualTo("Pérez");
        assertThat(guardado.getNumeroDocumento()).isEqualTo("12345678");
    }

    @Test
    @DisplayName("Debe encontrar cliente por tipo y número de documento")
    void debeEncontrarClientePorDocumento() {
        // arrange
        Cliente cliente = Cliente.builder()
                .nombre("María")
                .apellido("González")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("87654321")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.RESPONSABLE_INSCRIPTO)
                .direccion("Av. Las Heras 1234")
                .telefono("9876543210")
                .mail("maria@test.com")
                .direccionFiscal("Av. Las Heras 1234")
                .build();
        entityManager.persist(cliente);
        entityManager.flush();

        // Act
        Optional<Cliente> encontrado = clienteRepositorio
                .findByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, "87654321");

        // Assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("María");
        assertThat(encontrado.get().getApellido()).isEqualTo("González");
    }

    @Test
    @DisplayName("No debe encontrar cliente con documento inexistente")
    void noDebeEncontrarClienteInexistente() {
        Optional<Cliente> encontrado = clienteRepositorio
                .findByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, "99999999");

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Debe verificar existencia de cliente por documento")
    void debeVerificarExistenciaCliente() {
        Cliente cliente = Cliente.builder()
                .nombre("Pedro")
                .apellido("Ramírez")
                .tipoDocumento(TipoDocumento.CUIT)
                .numeroDocumento("2012345678")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.RESPONSABLE_INSCRIPTO)
                .direccion("San Martín 456")
                .telefono("1122334455")
                .mail("pedro@test.com")
                .direccionFiscal("San Martín 456")
                .build();
        entityManager.persist(cliente);
        entityManager.flush();

        boolean existe = clienteRepositorio
                .existsByTipoDocumentoAndNumeroDocumento(TipoDocumento.CUIT, "2012345678");

        assertThat(existe).isTrue();
    }

    @Test
    @DisplayName("No debe existir cliente con documento no registrado")
    void noDebeExistirClienteNoRegistrado() {
        boolean existe = clienteRepositorio
                .existsByTipoDocumentoAndNumeroDocumento(TipoDocumento.DNI, "00000000");

        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Debe listar clientes por estado de cuenta ACTIVA")
    void debeListarClientesPorEstadoActivo() {
        Cliente activo1 = Cliente.builder()
                .nombre("Ana")
                .apellido("López")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("11111111")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Belgrano 100")
                .telefono("1111111111")
                .mail("ana@test.com")
                .direccionFiscal("Belgrano 100")
                .build();
        
        Cliente activo2 = Cliente.builder()
                .nombre("Luis")
                .apellido("Martínez")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("22222222")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.MONOTRIBUTO)
                .direccion("Moreno 200")
                .telefono("2222222222")
                .mail("luis@test.com")
                .direccionFiscal("Moreno 200")
                .build();
        
        Cliente inactivo = Cliente.builder()
                .nombre("Carlos")
                .apellido("Fernández")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("33333333")
                .estadoCuenta(EstadoCuenta.INACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Sarmiento 300")
                .telefono("3333333333")
                .mail("carlos@test.com")
                .direccionFiscal("Sarmiento 300")
                .build();

        entityManager.persist(activo1);
        entityManager.persist(activo2);
        entityManager.persist(inactivo);
        entityManager.flush();

        List<Cliente> clientesActivos = clienteRepositorio.findByEstadoCuenta(EstadoCuenta.ACTIVA);

        assertThat(clientesActivos).hasSize(2);
        assertThat(clientesActivos).extracting(Cliente::getNombre)
                .containsExactlyInAnyOrder("Ana", "Luis");
    }

    @Test
    @DisplayName("Debe listar clientes por estado de cuenta INACTIVA")
    void debeListarClientesPorEstadoInactivo() {
        Cliente activo = Cliente.builder()
                .nombre("Roberto")
                .apellido("Silva")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("44444444")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Rivadavia 400")
                .telefono("4444444444")
                .mail("roberto@test.com")
                .direccionFiscal("Rivadavia 400")
                .build();
        
        Cliente inactivo1 = Cliente.builder()
                .nombre("Laura")
                .apellido("Gómez")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("55555555")
                .estadoCuenta(EstadoCuenta.INACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Mitre 500")
                .telefono("5555555555")
                .mail("laura@test.com")
                .direccionFiscal("Mitre 500")
                .build();

        entityManager.persist(activo);
        entityManager.persist(inactivo1);
        entityManager.flush();

        List<Cliente> clientesInactivos = clienteRepositorio.findByEstadoCuenta(EstadoCuenta.INACTIVA);

        assertThat(clientesInactivos).hasSize(1);
        assertThat(clientesInactivos.get(0).getNombre()).isEqualTo("Laura");
    }

    @Test
    @DisplayName("Debe retornar lista vacía si no hay clientes con el estado buscado")
    void debeRetornarListaVaciaSinClientesConEstado() {
        Cliente activo = Cliente.builder()
                .nombre("Sergio")
                .apellido("Torres")
                .tipoDocumento(TipoDocumento.DNI)
                .numeroDocumento("66666666")
                .estadoCuenta(EstadoCuenta.ACTIVA)
                .condIVA(CondicionIVA.CONSUMIDOR_FINAL)
                .direccion("Córdoba 600")
                .telefono("6666666666")
                .mail("sergio@test.com")
                .direccionFiscal("Córdoba 600")
                .build();

        entityManager.persist(activo);
        entityManager.flush();

        List<Cliente> inactivos = clienteRepositorio.findByEstadoCuenta(EstadoCuenta.INACTIVA);

        assertThat(inactivos).isEmpty();
    }
}
