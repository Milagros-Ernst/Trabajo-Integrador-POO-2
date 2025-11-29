package integrador.programa.modelo;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "detalle_recibo")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor @Builder
public class DetalleRecibo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalle_recibo")
    private Long idDetalleRecibo;  // ← LONG AUTO-INCREMENTAL

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recibo", nullable = false)
    private Recibo recibo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_factura", nullable = false)
    private Factura factura;

    @Column(name = "importe_aplicado", nullable = false)
    private Double importeAplicado;

    @Column(name = "saldo_pendiente_factura", nullable = false)
    private Double saldoPendienteFactura;
}
