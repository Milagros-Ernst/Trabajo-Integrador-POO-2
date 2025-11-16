package integrador.programa.modelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "DetalleFactura")
@EqualsAndHashCode(exclude = "factura")  
@ToString(exclude = "factura") 
public class DetalleFactura {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Setter(AccessLevel.NONE)
    private String idDetalle;

    @NotBlank(message = "La descripción no puede estar vacía.")
    @Size(min = 1, max = 250, message = "La descripción debe tener entre 1 y 250 caracteres.")
    @Column(name = "descripción")
    private String descripcion;

    // pensar en que descripcion sea de tipo servicio???
    // en realidad debería tomar por ahi la de servicio pero no ser tipo

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser positiva")
    @Column(name = "cantidad")
    private int cantidad;

    // relaciones en bd
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_factura", nullable = false)
    @NotNull(message = "El detalle debe pertenecer a una factura")
    private Factura factura;

    // Relación con Servicio 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    @NotNull(message = "El detalle debe estar asociado a un servicio")
    private Servicio servicio;

    // Relación con DetalleNota 
    @OneToOne(mappedBy = "detalleFactura", fetch = FetchType.LAZY)
    private DetalleNota detalleNota;
}
