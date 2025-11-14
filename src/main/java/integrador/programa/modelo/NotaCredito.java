package integrador.programa.modelo;

import java.time.LocalDate;
// Corregido: Se usa jakarta.persistence para ser compatible con tu Factura.java
import jakarta.persistence.*; 

/**
 * (AC 3)
 * Entidad que representa la Nota de Crédito.
 * Se genera automáticamente al anular una Factura para revertir
 * la operación y mantener la trazabilidad.
 */
@Entity
@Table(name = "notas_credito")
public class NotaCredito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idNotaCredito;

    @Column(nullable = false)
    private LocalDate fecha;

    /**
     * (AC 2)
     * Motivo por el cual se realiza la anulación.
     */
    @Column(nullable = false, length = 255) // Especificar un tamaño es buena práctica
    private String motivo;

    /**
     * (AC 2)
     * Nombre o ID del administrador/usuario que realizó la anulación.
     */
    @Column(nullable = false, length = 100)
    private String responsable;

    /**
     * (AC 3)
     * El monto total de la nota de crédito, que debe ser igual
     * al de la factura que anula.
     */
    @Column(nullable = false)
    private double total;

    /**
     * (AC 3 y AC 6)
     * Vínculo directo a la Factura que está siendo anulada.
     * Esta entidad (NotaCredito) es la "dueña" de la relación.
     * - @JoinColumn define la columna de clave foránea ('id_factura_anulada').
     * - 'referencedColumnName' es el nombre de la PK en la tabla 'facturas'.
     * - 'unique = true' asegura que una NC solo puede anular a UNA factura
     * y una Factura solo puede ser anulada por UNA NC.
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    // Corregido: "id_factura" debe coincidir con el @Column(name = "id_factura") de tu clase Factura
    @JoinColumn(name = "id_factura_anulada", referencedColumnName = "id_factura", unique = true)
    private Factura facturaAnulada;

    // --- Constructores ---

    /**
     * Constructor por defecto requerido por JPA.
     */
    public NotaCredito() {
    }

    /**
     * Constructor para crear una nueva Nota de Crédito.
     * Usado por el FacturaService.
     */
    public NotaCredito(LocalDate fecha, String motivo, String responsable, double total, Factura facturaAnulada) {
        this.fecha = fecha;
        this.motivo = motivo;
        this.responsable = responsable;
        this.total = total;
        this.facturaAnulada = facturaAnulada;
    }

    // --- Getters y Setters ---

    public int getIdNotaCredito() {
        return idNotaCredito;
    }

    public void setIdNotaCredito(int idNotaCredito) {
        this.idNotaCredito = idNotaCredito;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getResponsable() {
        return responsable;
    }

    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public Factura getFacturaAnulada() {
        return facturaAnulada;
    }

    public void setFacturaAnulada(Factura facturaAnulada) {
        this.facturaAnulada = facturaAnulada;
    }
}