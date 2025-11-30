package integrador.programa.repositorios;

import integrador.programa.modelo.Pago;
import integrador.programa.modelo.enumeradores.MetodoPago;
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
public class PagoRepositorioTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PagoRepositorio pagoRepositorio;

    @Test
    @DisplayName("Debe guardar y recuperar un pago")
    void debeGuardarYRecuperarPago() {
        // arrange
        Pago pago = Pago.builder()
                .importe(5000.0)
                .metodoPago(MetodoPago.TRANSFERENCIA)
                .fechaPago(LocalDate.now())
                .observaciones("Pago completo")
                .empleadoResponsable("Juan Pérez")
                .build();

        // act
        Pago guardado = pagoRepositorio.save(pago);

        // assert
        assertThat(guardado.getIdPago()).isNotNull();
        assertThat(guardado.getImporte()).isEqualTo(5000.0);
        assertThat(guardado.getMetodoPago()).isEqualTo(MetodoPago.TRANSFERENCIA);
        assertThat(guardado.getEmpleadoResponsable()).isEqualTo("Juan Pérez");
    }

    @Test
    @DisplayName("Debe encontrar pago por ID")
    void debeEncontrarPagoPorId() {
        // arrange
        Pago pago = Pago.builder()
                .importe(3000.0)
                .metodoPago(MetodoPago.EFECTIVO)
                .fechaPago(LocalDate.now())
                .observaciones("Pago en efectivo")
                .empleadoResponsable("María González")
                .build();
        entityManager.persist(pago);
        entityManager.flush();

        // act
        Optional<Pago> encontrado = pagoRepositorio.findById(pago.getIdPago());

        // assert
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getImporte()).isEqualTo(3000.0);
        assertThat(encontrado.get().getMetodoPago()).isEqualTo(MetodoPago.EFECTIVO);
    }

    @Test
    @DisplayName("No debe encontrar pago con ID inexistente")
    void noDebeEncontrarPagoInexistente() {
        Optional<Pago> encontrado = pagoRepositorio.findById(99999L);

        assertThat(encontrado).isEmpty();
    }

    @Test
    @DisplayName("Debe listar todos los pagos")
    void debeListarTodosLosPagos() {
        Pago pago1 = Pago.builder()
                .importe(1000.0)
                .metodoPago(MetodoPago.TRANSFERENCIA)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Admin1")
                .build();

        Pago pago2 = Pago.builder()
                .importe(2000.0)
                .metodoPago(MetodoPago.EFECTIVO)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Admin2")
                .build();

        Pago pago3 = Pago.builder()
                .importe(3000.0)
                .metodoPago(MetodoPago.CREDITO)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Admin3")
                .build();

        entityManager.persist(pago1);
        entityManager.persist(pago2);
        entityManager.persist(pago3);
        entityManager.flush();

        List<Pago> pagos = pagoRepositorio.findAll();

        assertThat(pagos).hasSize(3);
    }

    @Test
    @DisplayName("Debe actualizar un pago existente")
    void debeActualizarPago() {
        Pago pago = Pago.builder()
                .importe(2500.0)
                .metodoPago(MetodoPago.EFECTIVO)
                .fechaPago(LocalDate.now())
                .observaciones("Observación inicial")
                .empleadoResponsable("Ana Torres")
                .build();
        entityManager.persist(pago);
        entityManager.flush();

        pago.setObservaciones("Observación actualizada");
        pago.setImporte(3500.0);
        Pago actualizado = pagoRepositorio.save(pago);
        entityManager.flush();

        assertThat(actualizado.getObservaciones()).isEqualTo("Observación actualizada");
        assertThat(actualizado.getImporte()).isEqualTo(3500.0);
    }

    @Test
    @DisplayName("Debe contar el número total de pagos")
    void debeContarNumeroDePagos() {
        Pago pago1 = Pago.builder()
                .importe(1000.0)
                .metodoPago(MetodoPago.TRANSFERENCIA)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Test1")
                .build();

        Pago pago2 = Pago.builder()
                .importe(2000.0)
                .metodoPago(MetodoPago.EFECTIVO)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Test2")
                .build();

        entityManager.persist(pago1);
        entityManager.persist(pago2);
        entityManager.flush();

        long count = pagoRepositorio.count();

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("Debe guardar pago con diferentes métodos de pago")
    void debeGuardarPagosConDiferentesMetodos() {
        // arrange y act
        Pago efectivo = Pago.builder()
                .importe(1000.0)
                .metodoPago(MetodoPago.EFECTIVO)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Empleado1")
                .build();

        Pago transferencia = Pago.builder()
                .importe(2000.0)
                .metodoPago(MetodoPago.TRANSFERENCIA)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Empleado2")
                .build();

        Pago tarjeta = Pago.builder()
                .importe(3000.0)
                .metodoPago(MetodoPago.CREDITO)
                .fechaPago(LocalDate.now())
                .empleadoResponsable("Empleado3")
                .build();

        pagoRepositorio.save(efectivo);
        pagoRepositorio.save(transferencia);
        pagoRepositorio.save(tarjeta);

        // assert
        List<Pago> pagos = pagoRepositorio.findAll();
        assertThat(pagos).hasSize(3);
        assertThat(pagos).extracting(Pago::getMetodoPago)
                .containsExactlyInAnyOrder(
                        MetodoPago.EFECTIVO,
                        MetodoPago.TRANSFERENCIA,
                        MetodoPago.CREDITO
                );
    }

    @Test
    @DisplayName("Debe validar que la fecha de pago se establece correctamente")
    void debeValidarFechaDePago() {
        LocalDate fechaEsperada = LocalDate.of(2025, 11, 15);
        Pago pago = Pago.builder()
                .importe(4500.0)
                .metodoPago(MetodoPago.DEBITO)
                .fechaPago(fechaEsperada)
                .empleadoResponsable("Pedro Sánchez")
                .build();

        Pago guardado = pagoRepositorio.save(pago);

        assertThat(guardado.getFechaPago()).isEqualTo(fechaEsperada);
    }
}
