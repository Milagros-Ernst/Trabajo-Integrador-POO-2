package integrador.programa.modelo;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import integrador.programa.modelo.enumeradores.TipoComprobante;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "NotaCredito")
public class NotaCredito {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "idNota", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String nroNota;

    // me gustaría despues cambiar en el diagrama nroNota por idNota
    // para que sea cohesivo con factura

    @NotNull
    @Size(min = 4, max = 8)
    private String nroSerie;

    @NotBlank
    private double precioTotal;

    @NotNull
    @CreationTimestamp
    @Column(name = "Fecha de emision", nullable = false, updatable = false)
    private LocalDate fecha;

    @NotNull
    private TipoComprobante tipo;

}
