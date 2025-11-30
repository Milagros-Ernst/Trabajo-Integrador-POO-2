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

    // Genera número correlativo de recibo
    public Long generarNroRecibo() {
        Long ultimo = reciboRepositorio.obtenerUltimoNumero();
        return ultimo + 1;
    }

    // Busca por ID
    public Recibo buscarPorId(Long idRecibo) {
        return reciboRepositorio.findById(idRecibo)
                .orElseThrow(() -> new IllegalArgumentException("Recibo no encontrado"));
    }

    // Buscar por número de recibo
    public Recibo buscarPorNumero(Long nroRecibo) {
        Recibo r = reciboRepositorio.findByNroRecibo(nroRecibo);
        if (r == null) {
            throw new IllegalArgumentException("No existe un recibo con ese número");
        }
        return r;
    }

    // Recibos por cliente
    public List<Recibo> listarPorCliente(Cliente cliente) {
        return reciboRepositorio.findByCliente(cliente);
    }

    // Listar todos
    public List<Recibo> listarTodos() {
        return reciboRepositorio.findAll();
    }
}
