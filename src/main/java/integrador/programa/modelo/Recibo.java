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
@AllArgsConstructor
@Builder
public class Recibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recibo")
    private Long idRecibo;   // PK

    @Column(name = "nro_recibo", nullable = false, unique = true)
    private Long nroRecibo;  // número correlativo de recibo

    @CreationTimestamp
    @Column(name = "fecha_emision", nullable = false, updatable = false)
    private LocalDate fechaEmision;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "importe_total", nullable = false)
    private Double importeTotal;

    // 1 Recibo ----- N DetalleRecibo
    @OneToMany(mappedBy = "recibo",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<DetalleRecibo> detalles = new ArrayList<>();

    // 1 Recibo ----- 1 Pago
    @OneToOne(mappedBy = "recibo", cascade = CascadeType.ALL)
    private Pago pago;
    
    public void agregarDetalle(DetalleRecibo detalle) {
        detalles.add(detalle);
        detalle.setRecibo(this);
    }

    public void quitarDetalle(DetalleRecibo detalle) {
        detalles.remove(detalle);
        detalle.setRecibo(null);
    }
}
