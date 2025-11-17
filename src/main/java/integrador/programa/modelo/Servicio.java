package integrador.programa.modelo;

import org.hibernate.annotations.GenericGenerator;

import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Servicio")
public class Servicio {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_servicio", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE) // evita que Lombok genere setter (si lo usás a nivel de clase)
    private String idServicio;

    @NotBlank(message = "El nombre del servicio es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 5, max = 255, message = "La descripción debe tener entre 5 y 255 caracteres")
    @Column(name = "descripcion", nullable = false, length = 255)
    private String descripcion;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;


    @NotNull(message = "El tipo de IVA es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_iva", nullable = false, length = 20)
    private TipoIVA tipoIva;

    @NotNull(message = "El estado del servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_servicio", nullable = false, length = 20)
    private EstadoServicio estadoServicio; // ALTA o BAJ

    // se inicia por defecto con estado ALTA
    public Servicio() {
         this.estadoServicio = EstadoServicio.ALTA;
    }

    public Servicio(String nombre,
                    String descripcion,
                    Double precioUnitario,
                    TipoIVA tipoIva,
                    EstadoServicio estadoServicio) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precioUnitario = precioUnitario;
        this.tipoIva = tipoIva;
        this.estadoServicio = estadoServicio;
    }

}
