package integrador.programa.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recibo")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recibo")
    private Long idRecibo;   // ← LONG AUTO-INCREMENTAL

    @Column(name = "nro_recibo", nullable = false, unique = true)
    private Long nroRecibo;  // ← también LONG incremental correlativo

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDate fechaEmision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "importe_total", nullable = false)
    private Double importeTotal;

    // Relación con los detalles del recibo
    @OneToMany(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleRecibo> detalles = new ArrayList<>();

    // Pagos aplicados que conforman este recibo
    @ManyToMany
    @JoinTable(
            name = "recibo_pago",
            joinColumns = @JoinColumn(name = "id_recibo"),
            inverseJoinColumns = @JoinColumn(name = "id_pago")
    )
    private List<Pago> pagos = new ArrayList<>();


    public void agregarDetalle(DetalleRecibo det) {
        detalles.add(det);
        det.setRecibo(this);
    }

    public void agregarPago(Pago pago) {
        pagos.add(pago);
    }
}
