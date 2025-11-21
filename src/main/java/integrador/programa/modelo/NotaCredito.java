package integrador.programa.modelo;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.generator.EventType;

import integrador.programa.modelo.enumeradores.TipoComprobante;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
@Table(name = "NotaCredito")
public class NotaCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idNota")
    @Setter(AccessLevel.NONE)
    private Long nroNota;

    // me gustaría despues cambiar en el diagrama nroNota por idNota
    // para que sea cohesivo con factura

    @Column(name = "nro_serie", columnDefinition = "SERIAL", insertable = false, updatable = false)
    @Generated(event = EventType.INSERT)
    private Long nroSerie;

    @NotBlank
    private double precioTotal;

    @NotNull
    @CreationTimestamp
    @Column(name = "Fecha de emision", nullable = false, updatable = false)
    private LocalDate fecha;

    @NotNull
    private TipoComprobante tipo;

    @NotBlank
    @Size(min = 3, max = 100)
    @Column(name = "empleado_responsable", nullable = false, length = 100)
    private String empleadoResponsable;

    @NotBlank(message = "El motivo de anulación es obligatorio")
    @Column(name = "motivo_anulacion", nullable = false, length = 255)
    private String motivoAnulacion;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_factura_anulada", referencedColumnName = "id_factura", unique = true)
    private Factura facturaAnulada;

    // Relación con DetalleNota 
    @OneToMany(mappedBy = "notaCredito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private java.util.List<DetalleNota> detallesNota = new java.util.ArrayList<>();

}
