package integrador.programa.modelo.enumeradores;

public enum TipoIVA {

    IVA_21(21.0),
    IVA_105(10.5),
    IVA_0(0.0),
    IVA_27(27.0);

    private final double valor;

    TipoIVA(double valor) {
        this.valor = valor;
    }

    public double getValor() {
        return valor;
    }

    public String getTexto() {
        return valor + "%";
    }
}
