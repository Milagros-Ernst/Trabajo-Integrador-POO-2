package integrador.programa.modelo;

import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "Detalle_Recibo")
@Getter @Setter
@NoArgsConstructor
public class DetalleRecibo {

    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id_detalle_recibo", columnDefinition = "VARCHAR(36)")
    @Setter(AccessLevel.NONE)
    private String idDetalleRecibo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_recibo", nullable = false)
    private Recibo recibo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_factura", nullable = false)
    private Factura factura;

    @NotNull
    @Column(name = "importe_aplicado", nullable = false)
    private Double importeAplicado;

    @NotNull
    @Column(name = "saldo_pendiente_factura", nullable = false)
    private Double saldoPendienteFactura;
}
