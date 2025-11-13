package integrador.programa.servicios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import integrador.programa.repositorios.ClienteRepositorio;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ClienteServicio {

    private final ClienteRepositorio clienteRepositorio;

    public ClienteServicio(ClienteRepositorio clienteRepositorio) {
        this.clienteRepositorio = clienteRepositorio;
    }

    public Cliente crearCliente(@Valid Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el cliente con ID: " + id));
    }

    public Optional<Cliente> buscarPorDocumento(TipoDocumento tipoDocumento, String numeroDocumento) {
        return clienteRepositorio.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    public List<Cliente> listarTodos() {
        return clienteRepositorio.findAll();
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepositorio.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Cliente> buscarPorApellido(String apellido) {
        return clienteRepositorio.findByApellidoContainingIgnoreCase(apellido);
    }

    public Cliente actualizarCliente(@Valid Cliente cliente) {
        return clienteRepositorio.save(cliente);
    }

    public void eliminarCliente(Long id) {
        clienteRepositorio.deleteById(id);
    }
}