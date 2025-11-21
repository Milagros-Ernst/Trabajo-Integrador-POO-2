package integrador.programa.modelo;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "DetalleNota")
@EqualsAndHashCode(exclude = {"detalleFactura", "notaCredito"})
@ToString(exclude = {"detalleFactura", "notaCredito"})
public class DetalleNota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_nota", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private Long idDetalleNota;

    @NotNull(message = "La descripción no puede ser nula")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    @NotNull(message = "El precio no puede ser nulo")
    @Positive(message = "El precio debe ser positivo")
    @Column(name = "precio", nullable = false)
    private double precio;

    // Relación con DetalleFactura 
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_detalle_factura", referencedColumnName = "idDetalle", nullable = false, unique = true)
    private DetalleFactura detalleFactura;

    // Relación con NotaCredito 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_nota_credito", nullable = false)
    @NotNull(message = "El detalle de nota debe pertenecer a una nota de crédito")
    private NotaCredito notaCredito;
}

