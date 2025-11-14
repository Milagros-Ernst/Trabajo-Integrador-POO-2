package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.CondicionIVA;
//import integrador.programa.modelo.enumeradores.TipoDocumento;
//import integrador.programa.modelo.enumeradores.EstadoCuenta;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
@Table(name = "Cliente")
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
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne // O @ManyToOne si un cliente puede tener varias cuentas
    @JoinColumn(name = "id_cuenta")
    private Cuenta cuenta;
    
    public Cliente() {
        super();
    }
}

