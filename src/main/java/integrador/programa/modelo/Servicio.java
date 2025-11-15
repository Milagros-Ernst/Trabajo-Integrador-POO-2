package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.EstadoServicio;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import org.hibernate.annotations.GenericGenerator;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Entity                                        // Indica que esta clase es una entidad JPA
@Getter @Setter                                // Lombok genera getters y setters
@NoArgsConstructor                             // Lombok genera constructor vacío
@Table(name = "Servicio")                      // Nombre de la tabla en la BD
public class Servicio {

    // ID generado como UUID (igual que en Factura y DetalleFactura)
    @Id
    @GeneratedValue(generator = "uuid2")       // Genera el UUID
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_servicio", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)                  // No permite setear el ID manualmente
    private String idServicio;

    // Nombre del servicio — obligatorio
    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    // Descripción opcional (sin validación obligatoria)
    @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
    @Column(name = "descripcion", length = 255)
    private String descripcion;

    // Precio obligatorio — puede ser 0 o más
    @NotNull(message = "El precio unitario es obligatorio")
    @PositiveOrZero(message = "El precio unitario debe ser mayor o igual a 0")
    @Column(name = "precio_unitario", nullable = false)
    private Double precioUnitario;

    // Tipo de IVA obligatorio (21, 10.5, 0, etc.)
    @NotNull(message = "El tipo de IVA es obligatorio")
    @Column(name = "tipo_iva", nullable = false)
    private Double tipoIva;

    // Estado del servicio (ALTA o BAJA)
    @NotNull(message = "El estado del servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_servicio", nullable = false, length = 10)
    private EstadoServicio estadoServicio = EstadoServicio.ALTA; // valor por defecto
}
