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
import jakarta.persistence.*; // Usamos jakarta (estándar moderno)

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

    // NOTA: Se eliminó @NotBlank, ya que es para Strings.
    // Un 'double' primitivo no puede ser nulo.
    private double precioTotal;

    @NotNull
    @CreationTimestamp
    @Column(name = "Fecha de emision", nullable = false, updatable = false)
    private LocalDate fecha;

    @NotNull
    @Future
    private Date vencimiento;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente") // Asegúrate que este sea el nombre de tu FK en la BD
    private Cliente cliente;
    
    @NotNull
    @Enumerated(EnumType.STRING) // Buena práctica: guardar Enum como String
    // (AC 1) Se inicializa el estado por defecto
    private EstadoFactura estado = EstadoFactura.VIGENTE;

    @NotNull
    @Enumerated(EnumType.STRING) // Buena práctica: guardar Enum como String
    private TipoComprobante tipo;

    
    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DetalleFactura> detalles = new ArrayList<>();

    
    // --- ⬇️ INICIO: CAMPO NUEVO PARA HU 11 ⬇️ ---

    /**
     * (AC 6)
     * Vínculo a la Nota de Crédito que anula esta factura.
     * 'mappedBy = "facturaAnulada"' indica que la entidad NotaCredito
     * es la "dueña" de esta relación (contiene la clave foránea).
     * Lombok generará automáticamente getNotaCreditoAnulacion() y setNotaCreditoAnulacion().
     */
    @OneToOne(mappedBy = "facturaAnulada", fetch = FetchType.LAZY)
    private NotaCredito notaCreditoAnulacion;

    // --- ⬆️ FIN: CAMPO NUEVO PARA HU 11 ⬆️ ---


    // --- Métodos de Ayuda (Preservados) ---

    public void agregarDetalle(DetalleFactura detalle) {
        if (detalle != null) {
            // (OJO: Considera descomentar la siguiente línea para mantener la consistencia bidireccional)
            // detalle.setFactura(this);
            if (!this.detalles.contains(detalle)) {
                this.detalles.add(detalle);
            }
        }
    }

    public void removerDetalle(DetalleFactura detalle) {
        if (detalle != null) {
            this.detalles.remove(detalle);
            // (OJO: Considera descomentar la siguiente línea para mantener la consistencia bidireccional)
            // detalle.setFactura(null);
        }
    }

    public void limpiarDetalles() {
        this.detalles.clear();
    }
}