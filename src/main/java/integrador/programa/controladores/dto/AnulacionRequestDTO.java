package integrador.programa.controladores.dto;

/**
 * DTO (Objeto de Transferencia de Datos) para recibir los datos
 * del JSON en la petición de anulación (el "cuerpo" de la solicitud).
 */
public class AnulacionRequestDTO {

    // (AC 2)
    private String motivo;
    
    // (AC 2)
    private String responsable;

    // --- Getters y Setters ---
    // (Necesarios para que Spring/Jackson deserialice el JSON)

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
}