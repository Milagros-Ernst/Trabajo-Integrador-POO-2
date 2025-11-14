package integrador.programa.servicios.excepciones;

public class FacturaNoAnulableException extends RuntimeException {
    public FacturaNoAnulableException(String mensaje) {
        super(mensaje);
    }
}