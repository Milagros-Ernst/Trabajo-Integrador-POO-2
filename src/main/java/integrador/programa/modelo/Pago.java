package integrador.programa.modelo;

import java.time.LocalDate;

import integrador.programa.modelo.enumeradores.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "pago")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    @Setter(AccessLevel.NONE)
    private Long idPago;

    @NotNull(message = "El importe es obligatorio")
    @Positive(message = "El importe debe ser positivo")
    @Column(name = "importe", nullable = false)
    private Double importe;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    @NotNull(message = "La fecha de pago es obligatoria")
    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDate fechaPago = LocalDate.now();

    @Size(max = 150, message = "Las observaciones no pueden exceder los 150 caracteres")
    @Column(name = "observaciones", length = 150)
    private String observaciones;

    @NotBlank(message = "El empleado responsable es obligatorio")
    @Size(min = 3, max = 40, message = "El empleado responsable debe tener 40 caracteres como máximo")
    @Column(name = "empleado_responsable", nullable = false, length = 40)
    private String empleadoResponsable;

    // Un pago genera un recibo (1 a 1)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recibo", unique = true)
    private Recibo recibo;

    public String getDescripcionMetodoPago() {
        return this.metodoPago != null ? this.metodoPago.getDescripcion() : "";
    }
}
