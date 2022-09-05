package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.requests.UserEventRequest;

import java.util.List;

public interface IEventsService {

    Boolean crearEventoUsuario(String username, UserEventRequest userEvent);

    String obtenerIdUsuario(String username);

    Boolean existeUsuario(String username) throws InstantiationException, IllegalAccessException;

    boolean crearEventoPoste(Integer postId, List<Double> location, Integer zoneCode);

    void deleteAll();

    void deleteUser(String id);
}
