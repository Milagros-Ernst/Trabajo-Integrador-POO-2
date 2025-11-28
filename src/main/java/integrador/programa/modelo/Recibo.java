package integrador.programa.modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.generator.EventType;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Recibo")
@Getter @Setter
@NoArgsConstructor
public class Recibo {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_recibo", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String idRecibo;

    // Número de recibo correlativo independiente.
    @Column(name = "nro_recibo", columnDefinition = "SERIAL", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private Long nroRecibo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDate fechaEmision;

    @NotNull
    @Column(name = "importe_total", nullable = false)
    private Double importeTotal = 0.0;

    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecibo> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pago> pagos = new ArrayList<>();

    public void agregarDetalle(DetalleRecibo detalle) {
        if (!this.detalles.contains(detalle)) {
            this.detalles.add(detalle);
            detalle.setRecibo(this);
            recalcularImporteTotal();
        }
    }

    public void agregarPago(Pago pago) {
        if (!this.pagos.contains(pago)) {
            this.pagos.add(pago);
            pago.setRecibo(this);
        }
    }

    public void recalcularImporteTotal() {
        double total = 0.0;
        for (DetalleRecibo d : detalles) {
            total += d.getImporteAplicado();
        }
        this.importeTotal = total;
    }
}
