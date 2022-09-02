package app.retos.events.eventsreto.services;

import java.util.List;

public interface IEventsService {

    Boolean crearEventoUsuario(String username, List<Double> location);

    String obtenerIdUsuario(String username);

    boolean crearEventoPoste(Integer postId, List<Double> location, Integer zoneCode);
}
