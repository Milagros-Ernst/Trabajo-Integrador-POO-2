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

    @NotBlank(message = "El número de serie no puede estar vacío")
    @Size(min = 1, max = 250, message = "El número de serie debe tener entre 1 y 250 caracteres")
    @Column(name = "nro_serie")
    private String nroSerie;

    @NotNull(message = "El precio total no puede ser nulo")
    @Positive(message = "El precio total debe ser positivo")
    @Column(name = "precio_total")
    private double precioTotal;

    @NotNull(message = "La cantidad no puede ser nula")
    @Positive(message = "La cantidad debe ser positiva")
    @Column(name = "cantidad")
    private int cantidad;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_factura", nullable = false)
    @NotNull(message = "El detalle debe pertenecer a una factura")
    private Factura factura;
}
