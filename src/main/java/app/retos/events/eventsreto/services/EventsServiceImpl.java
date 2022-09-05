package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.clients.UsersFeignClient;
import app.retos.events.eventsreto.clients.ZonasFeignClient;
import app.retos.events.eventsreto.models.PostEvent;
import app.retos.events.eventsreto.models.UserEvent;
import app.retos.events.eventsreto.repository.*;
import app.retos.events.eventsreto.requests.UserEventRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class EventsServiceImpl implements IEventsService {

    @Autowired
    UserEventRepository userEventRepository;
    @Autowired
    PostEventRepository postEventRepository;
    @Autowired
    UsersFeignClient usersFeignClient;
    @Autowired
    ZonasFeignClient zonasFeignClient;
    @Autowired
    PhotoRepository photoRepository;
    @Autowired
    VideoRepository videoRepository;
    @Autowired
    AudioRepository audioRepository;
    @SuppressWarnings("rawtypes")
    @Autowired
    private CircuitBreakerFactory cbFactory;

    @Override
    public Boolean crearEventoUsuario(String username, UserEventRequest userEvent) {
        List<Double> location = userEvent.getLocation();
        String userId = obtenerIdUsuario(username);
        UserEvent events = new UserEvent();
        if (userEventRepository.existsByUserId(userId))
            events = userEventRepository.findByUserId(userId);
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
        if (userEvent.getComment() != null)
            events.setComment(userEvent.getComment());
        if (userEvent.getEventDescription() != null)
            events.setEventDescription(userEvent.getEventDescription());
        try {
            userEventRepository.save(events);
            audioRepository.deleteByEventId(events.getId());
            videoRepository.deleteByEventId(events.getId());
            photoRepository.deleteByEventId(events.getId());
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
    public Boolean existeUsuario(String username) throws InstantiationException, IllegalAccessException {
        return cbFactory.create("events").run(
                () -> usersFeignClient.preguntarUsuarioExiste(username),
                this::errorExisteUsername);
    }

    @Override
    public boolean crearEventoPoste(Integer postId, List<Double> location, Integer zoneCode) {
        PostEvent events = new PostEvent();
        if (postEventRepository.existsByPostId(postId))
            events = postEventRepository.findByPostId(postId);
        else {
            events.setPostId(postId);
            events.setType(1);
            events.setStatus(1);
            events.setZoneCode(zoneCode);
        }
        events.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        events.setTime(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        events.setLocation(location);
        try {
            postEventRepository.save(events);
            audioRepository.deleteByEventId(events.getId());
            videoRepository.deleteByEventId(events.getId());
            photoRepository.deleteByEventId(events.getId());
            return true;
        } catch (Exception e) {
            log.error("Error en la creación: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void deleteAll() {
        userEventRepository.deleteAll();
        photoRepository.deleteAll();
        videoRepository.deleteAll();
        audioRepository.deleteAll();
    }

    @Override
    public void deleteUser(String id) {
        userEventRepository.deleteById(id);
        audioRepository.deleteByEventId(id);
        videoRepository.deleteByEventId(id);
        photoRepository.deleteByEventId(id);
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

    private <T> T errorExisteUsername(Throwable e) {
        log.info("Error existe user: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio usuarios no esta disponible");
    }

}
