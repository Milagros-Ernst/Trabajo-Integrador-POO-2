package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.EstadoServicio;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "Servicio")
public class Servicio {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_servicio", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String idServicio;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    @NotNull(message = "El tipo de IVA es obligatorio")
    @Column(name = "tipo_iva", nullable = false)
    private Double tipoIva;      // 21.0, 10.5, 0.0, etc.

    @NotNull(message = "El estado del servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_servicio", nullable = false, length = 10)
    private EstadoServicio estadoServicio = EstadoServicio.ALTA;
}
