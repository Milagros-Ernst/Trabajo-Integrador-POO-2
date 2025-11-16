package integrador.programa.servicios;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.repositorios.ServicioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServicioServicio {

    @Autowired
    private final ServicioRepositorio servicioRepositorio;

    public ServicioServicio(ServicioRepositorio servicioRepositorio) {
        this.servicioRepositorio = servicioRepositorio;
    }

    // LISTAR TODOS
    public List<Servicio> listarTodos() {
        return servicioRepositorio.findAll();
    }

    // BUSCAR POR ID
    public Servicio buscarPorId(String id) {
        return servicioRepositorio.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("No se encontró el servicio con id: " + id));
    }

    // ALTA (crear nuevo)
    public Servicio agregarServicio(Servicio servicio) {
        // Si quisieras forzar un estado por defecto:
        if (servicio.getEstadoServicio() == null) {
            servicio.setEstadoServicio(EstadoServicio.ALTA);
        }
        return servicioRepositorio.save(servicio);
    }

    // MODIFICAR
    public Servicio actualizarServicio(String id, Servicio datosNuevos) {

        Servicio existente = buscarPorId(id);

        existente.setNombre(datosNuevos.getNombre());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setPrecioUnitario(datosNuevos.getPrecioUnitario());
        existente.setTipoIva(datosNuevos.getTipoIva());
        existente.setEstadoServicio(datosNuevos.getEstadoServicio());

        return servicioRepositorio.save(existente);
    }

    // CAMBIAR ESTADO → BAJA
    public Servicio darDeBaja(String id) {
        Servicio servicio = buscarPorId(id);
        servicio.setEstadoServicio(EstadoServicio.BAJA);
        return servicioRepositorio.save(servicio);
    }

    // CAMBIAR ESTADO → ALTA
    public Servicio darDeAlta(String id) {
        Servicio servicio = buscarPorId(id);
        servicio.setEstadoServicio(EstadoServicio.ALTA);
        return servicioRepositorio.save(servicio);
    }

    // ELIMINAR
    public void eliminarServicio(String id) {
        if (!servicioRepositorio.existsById(id)) {
            throw new IllegalArgumentException("No se puede eliminar. Servicio no encontrado con id: " + id);
        }
        servicioRepositorio.deleteById(id);
    }
}
