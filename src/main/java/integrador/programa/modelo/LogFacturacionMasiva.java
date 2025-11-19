package integrador.programa.modelo;

import java.time.LocalDate;
import java.time.Month;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;


@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "facturacion_masiva")
public class LogFacturacionMasiva {
    
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_facturacion_masiva", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String id;
    
    @NotNull(message = "La fecha de ejecución es obligatoria")
    @Column(name = "fecha_ejecucion", nullable = false)
    private LocalDate fechaEjecucion = LocalDate.now();

    @NotNull(message = "El período facturado es obligatorio")
    @Column(name = "periodo", nullable = false)
    private int periodo; // Guardar el mes como número (1=Enero, 12=Diciembre)

    @NotNull(message = "La cantidad de facturas generadas es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser positiva")
    private int cantidadFacturas;

    @NotBlank(message = "El responsable es obligatorio")
    private String empleadoResponsable;
    
    @Column(name = "servicios_incluidos")
    private String serviciosIncluidos; 

    public LogFacturacionMasiva(int periodo, int cantidadFacturas, String serviciosIncluidos, String empleadoResponsable) {
        this.periodo = periodo; // 1=Enero, 3=Marzo, etc.
        this.cantidadFacturas = cantidadFacturas;
        this.serviciosIncluidos = serviciosIncluidos;
        this.empleadoResponsable = empleadoResponsable;
    }
}