package integrador.programa.servicios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.Recibo;
import integrador.programa.repositorios.ReciboRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReciboServicio {

    private final ReciboRepositorio reciboRepositorio;

    public ReciboServicio(ReciboRepositorio reciboRepositorio) {
        this.reciboRepositorio = reciboRepositorio;
    }

    // Obtiene un recibo por su ID.
    public Recibo buscarPorId(Long idRecibo) {
    return reciboRepositorio.findById(idRecibo)
            .orElseThrow(() -> new IllegalArgumentException("Recibo no encontrado"));
}


    // Obtiene un recibo por su número correlativo.
    public Recibo buscarPorNumero(Long nroRecibo) {
        Recibo recibo = reciboRepositorio.findByNroRecibo(nroRecibo);
        if (recibo == null) {
            throw new IllegalArgumentException("No existe un recibo con ese número");
        }
        return recibo;
    }

    // Lista todos los recibos de un cliente (estado de cuenta).
    public List<Recibo> listarPorCliente(Cliente cliente) {
        return reciboRepositorio.findByCliente(cliente);
    }

    // Lista todos los recibos existentes (solo para administración si lo necesitás).
    public List<Recibo> listarTodos() {
        return reciboRepositorio.findAll();
    }
}
