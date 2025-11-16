package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.EstadoCuenta;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.Builder;

import java.util.Objects;

@Entity
@Table(name = "Cuenta")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Cuenta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cuenta")
    private Long idCuenta;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras y espacios")
    @Column(name = "apellido", nullable = false, length = 100)
    private String apellido;

    @NotNull(message = "El tipo de documento es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Pattern(regexp = "^[0-9]{7,11}$", message = "El número de documento debe contener entre 7 y 11 dígitos")
    @Column(name = "numero_documento", nullable = false, unique = true, length = 11)
    private String numeroDocumento;

    @NotNull(message = "El estado de la cuenta es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuenta", nullable = false, length = 20)
    @Builder.Default
    private EstadoCuenta estadoCuenta = EstadoCuenta.ACTIVA;

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public void activar() {
        this.estadoCuenta = EstadoCuenta.ACTIVA;
    }

    public void desactivar() {
        this.estadoCuenta = EstadoCuenta.INACTIVA;
    }

    public boolean estaActiva() {
        return this.estadoCuenta == EstadoCuenta.ACTIVA;
    }

    public boolean validarDocumento() {
        if (numeroDocumento == null || tipoDocumento == null) {
            return false;
        }
        switch (tipoDocumento) {
            case DNI:
                return numeroDocumento.matches("^[0-9]{7,8}$");
            case CUIT:
            case CUIL:
                return numeroDocumento.matches("^[0-9]{11}$");
            default:
                return false;
        }
    }

    // equals y hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cuenta cuenta = (Cuenta) o;
        return Objects.equals(numeroDocumento, cuenta.numeroDocumento) &&
            tipoDocumento == cuenta.tipoDocumento;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numeroDocumento, tipoDocumento);
    }

    // toString
    @Override
    public String toString() {
        return "Cuenta{" +
                "idCuenta=" + idCuenta +
                ", nombre='" + nombre + '\'' +
                ", apellido='" + apellido + '\'' +
                ", tipoDocumento=" + tipoDocumento +
                ", numeroDocumento='" + numeroDocumento + '\'' +
                ", estadoCuenta=" + estadoCuenta +
                '}';
    }
}
