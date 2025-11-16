package integrador.programa.modelo;

import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.modelo.enumeradores.TipoIVA;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "Servicio")
public class Servicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servicio")
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

    //Getters y Setters requeridos por ServicioServicio

    public String getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(String idServicio) {
        this.idServicio = idServicio;
    }

    public String getNombre() {                
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {           
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getPrecioUnitario() {        
        return precioUnitario;
    }

    public void setPrecioUnitario(Double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public TipoIVA getTipoIva() {              
        return tipoIva;
    }

    public void setTipoIva(TipoIVA tipoIva) {
        this.tipoIva = tipoIva;
    }

    public EstadoServicio getEstadoServicio() { 
        return estadoServicio;
    }

    public void setEstadoServicio(EstadoServicio estadoServicio) { 
        this.estadoServicio = estadoServicio;
    }

    @Override
    public String toString() {
        return "Servicio{" +
                "idServicio=" + idServicio +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precioUnitario=" + precioUnitario +
                ", tipoIva=" + tipoIva +
                ", estadoServicio=" + estadoServicio +
                '}';
    }
}
