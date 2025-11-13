package integrador.programa.modelo;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

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
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_factura", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String idFactura;
    
    @NotBlank
    @Size(min = 4, max = 8)
    private String nroSerie;

    @NotBlank
    private double precioTotal;

    @NotNull
    @CreationTimestamp
    @Column(name = "Fecha de emision", nullable = false, updatable = false)
    private LocalDate fecha;

    @NotNull
    @Future
    private Date vencimiento;

    @NotNull
    private EstadoFactura estado;

    @NotNull
    private TipoComprobante tipo;

    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles = new ArrayList<>();


    public void agregarDetalle(DetalleFactura detalle) {
        if (detalle != null) {
            detalle.setFactura(this);
            if (!this.detalles.contains(detalle)) {
                this.detalles.add(detalle);
            }
        }
    }

    public void removerDetalle(DetalleFactura detalle) {
        if (detalle != null) {
            this.detalles.remove(detalle);
            detalle.setFactura(null);
        }
    }

    public void limpiarDetalles() {
        this.detalles.clear();
    }
}
