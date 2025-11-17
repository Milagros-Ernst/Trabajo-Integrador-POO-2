package integrador.programa.modelo;

import java.time.LocalDate;

import org.hibernate.annotations.GenericGenerator;

import integrador.programa.modelo.enumeradores.EstadoServicio;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Entity
@Table(name = "cliente_servicio") 
@Getter@Setter@NoArgsConstructor
public class ClienteServicio {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_cliente_servicio", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String idClienteServicio;

    @NotNull(message = "El cliente es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @NotNull(message = "El servicio es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_servicio", nullable = false)
    private Servicio servicio;

    @NotNull(message = "El estado del servicio es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "estado_servicio", nullable = false, length = 20)
    private EstadoServicio estadoServicio;

    @NotNull(message = "La fecha de alta es obligatoria")
    @Column(name = "fecha_alta", nullable = false)
    private LocalDate fechaAlta;

    @Column(name = "fecha_baja")
    private LocalDate fechaBaja;

    // Da de alta este servicio para el cliente.
    public void darDeAlta(LocalDate fechaAlta) {
        this.estadoServicio = EstadoServicio.ALTA;
        this.fechaAlta = fechaAlta;
        this.fechaBaja = null;
    }

    // Da de baja este servicio para el cliente.
    public void darDeBaja(LocalDate fechaBaja) {
        this.estadoServicio = EstadoServicio.BAJA;
        this.fechaBaja = fechaBaja;
    }

    // Indica si el servicio está activo para este cliente.
    public boolean estaActivo() {
        return this.estadoServicio == EstadoServicio.ALTA && this.fechaBaja == null;
    }

    @Override
    public String toString() {
        return "ClienteServicio{" +
                "idClienteServicio=" + idClienteServicio +
                ", cliente=" + (cliente != null ? cliente.getNombre() : "null") +
                ", servicio=" + (servicio != null ? servicio.getNombre() : "null") +
                ", estadoServicio=" + estadoServicio +
                ", fechaAlta=" + fechaAlta +
                ", fechaBaja=" + fechaBaja +
                '}';
    }
}
