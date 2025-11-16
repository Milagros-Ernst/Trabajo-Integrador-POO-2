package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.CondicionIVA;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cliente extends Cuenta {

    @NotNull(message = "La condición frente al IVA es obligatoria")
    @Enumerated(EnumType.STRING)
    @Column(name = "condicion_iva", nullable = false, length = 30)
    private CondicionIVA condIVA;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 150, message = "La dirección debe tener entre 5 y 150 caracteres")
    @Column(name = "direccion", nullable = false, length = 150)
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\-\\s()+]{6,20}$", message = "El teléfono contiene caracteres no válidos")
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    @Column(name = "mail", nullable = false, length = 100)
    private String mail;

    @NotBlank(message = "La dirección fiscal es obligatoria")
    @Size(min = 5, max = 150, message = "La dirección fiscal debe tener entre 5 y 150 caracteres")
    @Column(name = "direccion_fiscal", nullable = false, length = 150)
    private String direccionFiscal;

    // lo relaciono con factura
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<Factura> facturas = new java.util.ArrayList<>();
}
