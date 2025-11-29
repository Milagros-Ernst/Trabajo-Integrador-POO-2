package integrador.programa.modelo;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

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

    // UNO A UNO con DetalleRecibo
    @OneToOne(mappedBy = "recibo", cascade = CascadeType.ALL, orphanRemoval = true)
    private DetalleRecibo detalle;

    // UNO A UNO con Pago
    @OneToOne(mappedBy = "recibo", cascade = CascadeType.ALL)
    private Pago pago;

}
