package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.clients.UsersFeignClient;
import app.retos.events.eventsreto.clients.ZonasFeignClient;
import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class EventsServiceImpl implements IEventsService{

    @SuppressWarnings("rawtypes")
    @Autowired
    private CircuitBreakerFactory cbFactory;

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UsersFeignClient usersFeignClient;

    @Autowired
    ZonasFeignClient zonasFeignClient;

    @Override
    public Boolean crearEventoUsuario(String username, List<Double> location) {
        String userId = obtenerIdUsuario(username);
        Events events = new Events();
        if(eventRepository.existsByUserId(userId))
            events = eventRepository.findByUserId(userId);
        else {
            events.setUserId(userId);
            events.setType(1);
            events.setStatus(1);
        }
        events.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        events.setTime(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        events.setLocation(location);
        events.setZoneCode(cbFactory.create("events").run(
                () -> zonasFeignClient.obtainZonesEvents(userId, location),
                this::errorObtenerZona));
        events.setPhotos(new ArrayList<>());
        events.setVideos(new ArrayList<>());
        events.setAudios(new ArrayList<>());
        try {
            eventRepository.save(events);
            return true;
        } catch (Exception e) {
            log.error("Error en la creación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public String obtenerIdUsuario(String username) {
        return cbFactory.create("events").run(
                () -> usersFeignClient.obtenerId(username),
                this::errorObtenerUsername);
    }

    @Override
    public boolean crearEventoPoste(Integer postId, List<Double> location, Integer zoneCode) {
        Events events = new Events();
        if(eventRepository.existsByPostId(postId))
            events = eventRepository.findByPostId(postId);
        else {
            events.setPostId(postId);
            events.setType(1);
            events.setStatus(1);
            events.setZoneCode(zoneCode);
        }
        events.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        events.setTime(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        events.setLocation(location);
        events.setPhotos(new ArrayList<>());
        events.setVideos(new ArrayList<>());
        events.setAudios(new ArrayList<>());
        try {
            eventRepository.save(events);
            return true;
        } catch (Exception e) {
            log.error("Error en la creación: " + e.getMessage());
            return false;
        }
    }

    //  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //
    private String errorObtenerUsername(Throwable e) {
        log.info("Error obtener username: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio usuarios no esta disponible");
    }
    private Integer errorObtenerZona(Throwable e) {
        log.info("Error obtener zona: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio zona no esta disponible");
    }

}
