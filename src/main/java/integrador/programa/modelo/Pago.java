package integrador.programa.modelo;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import integrador.programa.modelo.enumeradores.MetodoPago;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.AccessLevel;
import lombok.Builder;

@Entity
@Table(name = "Pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    @Setter(AccessLevel.NONE)
    private Long idPago;

    @Size(max = 50, message = "El número de recibo no puede exceder los 50 caracteres")
    @Column(name = "nro_recibo", length = 50)
    private String nroRecibo;

    @NotNull(message = "El importe es obligatorio")
    @Positive(message = "El importe debe ser positivo")
    @Column(name = "importe", nullable = false)
    private Double importe;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false, length = 30)
    private MetodoPago metodoPago;

    @NotNull(message = "La fecha de pago es obligatoria")
    @CreationTimestamp
    @Column(name = "fecha_pago", nullable = false, updatable = false)
    private LocalDate fechaPago;

    @Size(max = 150, message = "Las observaciones no pueden exceder los 150 caracteres")
    @Column(name = "observaciones", length = 150)
    private String observaciones;

    @NotBlank(message = "El empleado responsable es obligatorio")
    @Size(min = 3, max = 40, message = "El empleado responsable debe tener 40 caracteres como máximo")
    @Column(name = "empleado_responsable", nullable = false, length = 40)
    private String empleadoResponsable;

    // pago asociado a una factura
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_factura", nullable = false)
    @NotNull(message = "El pago debe estar asociado a una factura")
    private Factura factura;

    // Valida si el importe del pago no excede el total de la factura
    public boolean validarImporte() {
        if (this.factura == null) {
            return false;
        }
        return this.importe != null && this.importe > 0 && this.importe <= this.factura.getPrecioTotal();
    }

    public boolean esPagoTotal() {
        if (this.factura == null || this.importe == null) {
            return false;
        }
        return Math.abs(this.importe - this.factura.getPrecioTotal()) < 0.01;
    }

    public boolean esPagoParcial() {
        return !esPagoTotal() && validarImporte();
    }

    // Obtiene el nombre completo del método de pago
    public String getDescripcionMetodoPago() {
        return this.metodoPago != null ? this.metodoPago.getDescripcion() : "";
    }
}
