package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.CondicionIVA;
//import integrador.programa.modelo.enumeradores.TipoDocumento;
import jakarta.validation.constraints.*;

public class Cliente extends Cuenta {

    @NotNull(message = "La condición frente al IVA es obligatoria")
    private CondicionIVA condIVA;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(min = 5, max = 150, message = "La dirección debe tener entre 5 y 150 caracteres")
    private String direccion;

    @NotBlank(message = "El teléfono es obligatorio")
    @Pattern(regexp = "^[0-9\\-\\s()+]{6,20}$", message = "El teléfono contiene caracteres no válidos")
    private String telefono;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String mail;

    @NotBlank(message = "La dirección fiscal es obligatoria")
    @Size(min = 5, max = 150, message = "La dirección fiscal debe tener entre 5 y 150 caracteres")
    private String direccionFiscal;

    
    public Cliente() {
        super();
    }
}

