package integrador.programa.servicios;

import integrador.programa.modelo.Cliente;
import integrador.programa.modelo.enumeradores.TipoDocumento;
import integrador.programa.modelo.enumeradores.EstadoCuenta;
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
        if (clienteRepositorio.existsByTipoDocumentoAndNumeroDocumento(
                cliente.getTipoDocumento(), cliente.getNumeroDocumento())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese documento");
        }
        
        return clienteRepositorio.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return clienteRepositorio.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("No se encontró el cliente con ID: " + id));
    }

    public Optional<Cliente> buscarPorDocumento(TipoDocumento tipoDocumento, String numeroDocumento) {
        return clienteRepositorio.findByTipoDocumentoAndNumeroDocumento(tipoDocumento, numeroDocumento);
    }

    public Cliente modificarCliente(Long id, @Valid Cliente clienteActualizado) {
        Cliente clienteExistente = buscarPorId(id);
        // se valida que el documento no esté en uso por otro cliente
        if (!clienteExistente.getNumeroDocumento().equals(clienteActualizado.getNumeroDocumento()) ||
            clienteExistente.getTipoDocumento() != clienteActualizado.getTipoDocumento()) {
            Optional<Cliente> clienteConDocumento = buscarPorDocumento(
                clienteActualizado.getTipoDocumento(), 
                clienteActualizado.getNumeroDocumento()
            );
            if (clienteConDocumento.isPresent() && !clienteConDocumento.get().getIdCuenta().equals(id)) {
                throw new IllegalArgumentException("El documento ya está en uso por otro cliente");
            }
        }

        clienteActualizado.setIdCuenta(id);
        return clienteRepositorio.save(clienteActualizado);
    }

    public Cliente bajaCliente(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.desactivar();
        return clienteRepositorio.save(cliente);
    }

    public Cliente reactivarCliente(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.activar();
        return clienteRepositorio.save(cliente);
    }

    public List<Cliente> listarClientesActivos() {
        return clienteRepositorio.findByEstadoCuenta(EstadoCuenta.ACTIVA);
    }

    public List<Cliente> listarClientesInactivos() {
        return clienteRepositorio.findByEstadoCuenta(EstadoCuenta.INACTIVA);
    }
}