package integrador.programa.repositorios;

import integrador.programa.modelo.LogFacturacionMasiva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LogFacturacionMasRepositorio extends JpaRepository<LogFacturacionMasiva, Long>{

    List<LogFacturacionMasiva> findByPeriodoOrderByFechaEjecucionDesc(int periodo);

    List<LogFacturacionMasiva> findByFechaEjecucionOrderByIdDesc(LocalDate fechaEjecucion);

    List<LogFacturacionMasiva> findByPeriodoAndFechaEjecucionOrderByIdDesc(int periodo, LocalDate fechaEjecucion);
}