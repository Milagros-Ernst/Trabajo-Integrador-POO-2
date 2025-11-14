package integrador.programa.servicios.excepciones;

public class FacturaNoEncontradaException extends RuntimeException {
    public FacturaNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}