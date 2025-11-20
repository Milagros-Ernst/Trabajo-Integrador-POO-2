package integrador.programa.servicios;

import integrador.programa.modelo.Servicio;
import integrador.programa.modelo.enumeradores.EstadoServicio;
import integrador.programa.repositorios.ServicioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServicioServicio {

    @Autowired
    private final ServicioRepositorio servicioRepositorio;

    public ServicioServicio(ServicioRepositorio servicioRepositorio) {
        this.servicioRepositorio = servicioRepositorio;
    }

    // LISTAR TODOS LOS SERVICIOS CON ESTADO ALTA
    public List<Servicio> listarTodos() {
        return servicioRepositorio.findByEstadoServicio(EstadoServicio.ALTA);
    }

    // BUSCAR POR ID
    public Servicio buscarPorId(String id) {
        return servicioRepositorio.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("No se encontró el servicio con id: " + id));
    }

    // ALTA (crear nuevo)
    @Transactional
    public Servicio agregarServicio(Servicio servicio) {
        // Estado por defecto ALTA si viene nulo
        if (servicio.getEstadoServicio() == null) {
            servicio.setEstadoServicio(EstadoServicio.ALTA);
        }
        return servicioRepositorio.save(servicio);
    }

    // MODIFICAR
    @Transactional
    public Servicio actualizarServicio(String id, Servicio datosNuevos) {

        Servicio existente = buscarPorId(id);

        existente.setNombre(datosNuevos.getNombre());
        existente.setDescripcion(datosNuevos.getDescripcion());
        existente.setPrecioUnitario(datosNuevos.getPrecioUnitario());
        existente.setTipoIva(datosNuevos.getTipoIva());
        existente.setEstadoServicio(datosNuevos.getEstadoServicio());

        return servicioRepositorio.save(existente);
    }

    // CAMBIAR ESTADO → ALTA
    @Transactional
    public Servicio darDeAlta(String id) {
        Servicio servicio = buscarPorId(id);
        servicio.setEstadoServicio(EstadoServicio.ALTA);
        return servicioRepositorio.save(servicio);
    }

    // "ELIMINAR" = BAJA LÓGICA
    @Transactional
    public void eliminarServicio(String id) {
        Servicio servicio = buscarPorId(id);
        servicio.setEstadoServicio(EstadoServicio.BAJA);
        servicioRepositorio.save(servicio);
    }
}
