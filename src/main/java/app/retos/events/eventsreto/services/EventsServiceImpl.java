package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.clients.HistoricalFeignClient;
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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class EventsServiceImpl implements IEventsService {

    @Autowired
    EventRepository eventRepository;
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

    @Autowired
    HistoricalFeignClient historicalFeignClient;
    @SuppressWarnings("rawtypes")
    @Autowired
    private CircuitBreakerFactory cbFactory;

    @Override
    public Boolean crearEventoUsuario(String username, UserEventRequest userEvent) {
        List<Double> location = new ArrayList<>(Arrays.asList(
                BigDecimal.valueOf(userEvent.getLocation().get(0)).setScale(5, RoundingMode.HALF_UP).doubleValue(),
                BigDecimal.valueOf(userEvent.getLocation().get(1)).setScale(5, RoundingMode.HALF_UP).doubleValue()));
        String userId = obtenerIdUsuario(username);
        UserEvent events = new UserEvent();

        if (userEventRepository.existsByUserId(userId)) {
            events = userEventRepository.findByUserId(userId);
            Integer zoneCode = events.getZone();
            events.setZone(cbFactory.create("events").run(
                    () -> zonasFeignClient.obtainZonesEventsManyTimes(userId, location, zoneCode),
                    this::errorObtenerZona));
        } else {
            events.setUserId(userId);
            events.setEventOrigin(1);
            events.setStatus(1);
            events.setZone(cbFactory.create("events").run(
                    () -> zonasFeignClient.obtainZonesEvents(userId, location),
                    this::errorObtenerZona));
        }
        if (userEvent.getTypeEmergency() == null) events.setTypeEmergency(1);
        else events.setTypeEmergency(userEvent.getTypeEmergency());
        events.setDate(new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime()));
        events.setTime(new SimpleDateFormat("HH:mm:ss").format(Calendar.getInstance().getTime()));
        events.setLocation(location);

        if (userEvent.getComment() != null)
            events.setComment(userEvent.getComment());
        try {
            UserEvent finalEvents = events;
            events.setHistoricalId(cbFactory.create("events").run(
                    () -> historicalFeignClient.crearHistorico(finalEvents.getUserId(), finalEvents.getEventOrigin(), finalEvents.getDate(),
                            finalEvents.getTime(), finalEvents.getTypeEmergency(), finalEvents.getLocation(),
                            finalEvents.getStatus(), finalEvents.getComment(), finalEvents.getZone()),
                    this::errorObtenerHistorical));
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
            events.setEventOrigin(1);
            events.setStatus(1);
            events.setZone(zoneCode);
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
        eventRepository.deleteAll();
        userEventRepository.deleteAll();
        postEventRepository.deleteAll();
        audioRepository.deleteAll();
        photoRepository.deleteAll();
        videoRepository.deleteAll();
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

    private String errorObtenerHistorical(Throwable e) {
        log.info("Error al crear historico: " + e.getMessage());
        return "-1";
    }

}
