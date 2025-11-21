package integrador.programa.modelo;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

import integrador.programa.modelo.enumeradores.EstadoFactura;
import integrador.programa.modelo.enumeradores.TipoComprobante;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;


@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "Factura")
public class Factura {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_factura", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private Long idFactura;
    
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long nroSerie;

    @NotNull(message = "El precio total es obligatorio")
    private double precioTotal;

    @NotNull
    @CreationTimestamp
    @Column(name = "Fecha de emision", nullable = false, updatable = false)
    private LocalDate fecha;

    @NotNull
    @Future
    private LocalDate vencimiento;

    @NotNull
    private EstadoFactura estado;

    @NotNull
    private TipoComprobante tipo;

    @NotNull
    private int periodo;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "empleado_responsable", nullable = false, length = 100)
    private String empleadoResponsable;
    // hardcodeariamos un nombre

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles = new ArrayList<>();

    // Relación con NotaCredito. le puse optional por si no hay nota
    @OneToOne(mappedBy = "facturaAnulada", fetch = FetchType.LAZY, optional = true)
    private NotaCredito notaCredito;

    // Relación con Cliente 
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    @NotNull(message = "La factura debe estar asociada a un cliente")
    private Cliente cliente;


    public void agregarDetalle(DetalleFactura detalle) {
        if (detalle != null) {
            // detalle.setFactura(this);
            if (!this.detalles.contains(detalle)) {
                this.detalles.add(detalle);
            }
        }
    }

    public void removerDetalle(DetalleFactura detalle) {
        if (detalle != null) {
            this.detalles.remove(detalle);
            // detalle.setFactura(null);
        }
    }

    public void limpiarDetalles() {
        this.detalles.clear();
    }

    public void calcularTotal() {
        double subtotalAcumulado = 0.0;
        double ivaAcumulado = 0.0;
        
        if (this.detalles == null) {
            this.precioTotal = 0.0;
            return;
        }

        for (DetalleFactura detalle : this.detalles) {
            subtotalAcumulado += detalle.getPrecio(); 
            Servicio servicio = detalle.getServicio();
            if (servicio != null) {
                double ivaDeEsteDetalle = detalle.getPrecio() * servicio.getTipoIva().getValor();
                ivaAcumulado += ivaDeEsteDetalle;
            }
        }
        
        this.precioTotal = subtotalAcumulado + ivaAcumulado;
    }

   

}
