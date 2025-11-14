package integrador.programa.controladores.excepciones;

// Importamos las excepciones del servicio
import integrador.programa.servicios.excepciones.FacturaNoAnulableException;
import integrador.programa.servicios.excepciones.FacturaNoEncontradaException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Manejador de excepciones global (@ControllerAdvice).
 * Captura las excepciones lanzadas por los Servicios
 * y las convierte en respuestas HTTP 4xx con un mensaje claro.
 */
@ControllerAdvice
public class RestExceptionHandler {

    /**
     * Captura la excepción cuando se intenta anular una factura
     * que no está VIGENTE o ya está ANULADA.
     * Devuelve un error HTTP 400 (Bad Request). (AC 1 y AC 4)
     */
    @ExceptionHandler(FacturaNoAnulableException.class)
    public ResponseEntity<String> handleFacturaNoAnulable(FacturaNoAnulableException ex, WebRequest request) {
        // Devuelve el mensaje de la excepción (ej. "La factura ya está anulada")
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Captura la excepción cuando no se encuentra una factura
     * por su ID.
     * Devuelve un error HTTP 404 (Not Found).
     */
    @ExceptionHandler(FacturaNoEncontradaException.class)
    public ResponseEntity<String> handleFacturaNoEncontrada(FacturaNoEncontradaException ex, WebRequest request) {
        // Devuelve el mensaje de la excepción (ej. "No se encontró la factura...")
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    /**
     * Opcional: Un manejador genérico para cualquier otra excepción
     * que no hayamos controlado.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex, WebRequest request) {
        // Devuelve un error HTTP 500 (Internal Server Error)
        return new ResponseEntity<>("Ocurrió un error interno en el servidor.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}