package app.retos.events.eventsreto.Controllers;

import app.retos.events.eventsreto.clients.SensoresFeignClient;
import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.repository.EventRepository;
import app.retos.events.eventsreto.repository.PostEventRepository;
import app.retos.events.eventsreto.repository.UserEventRepository;
import app.retos.events.eventsreto.requests.UserEventRequest;
import app.retos.events.eventsreto.services.IEventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/eventos")
public class EventsController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    PostEventRepository postEventRepository;
    @Autowired
    IEventsService eventsService;
    @Autowired
    SensoresFeignClient sensoresFeignClient;
    @SuppressWarnings("rawtypes")
    @Autowired
    private CircuitBreakerFactory cbFactory;

    @GetMapping("/listar")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Events> listarEventos() {
        return eventRepository.findAll();
    }

    @GetMapping("/listar/usuario/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public Events listarEventosUsuario(@PathVariable("username") String username) {
        String id = eventsService.obtenerIdUsuario(username);
        return userEventRepository.findByUserId(id);
    }

    @GetMapping("/listar/poste/{postId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Events listarEventosPoste(@PathVariable("postId") Integer postId) {
        return postEventRepository.findByPostId(postId);
    }

    @GetMapping("/listar/zona/{zoneCode}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Events> listarEventosZona(@PathVariable("zoneCode") Integer zoneCode) {
        return eventRepository.findByZoneCode(zoneCode);
    }

    @GetMapping("/listar/status/{status}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Events> listarEventosStatus(@PathVariable("status") Integer status) {
        return eventRepository.findByStatus(status);
    }

    @PostMapping("/crear/usuario/{username}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String crearEventoUser(@PathVariable("username") String username,
                                  @RequestBody @Validated UserEventRequest userEvent) throws InstantiationException, IllegalAccessException {
        if (eventsService.existeUsuario(username)) {
            if (eventsService.crearEventoUsuario(username, userEvent)) return "Evento creado correctamente";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creación del evento");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario: " + username + " no existe");
    }

    @PutMapping("/crear/poste/{postId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String crearEventoPoste(@PathVariable("postId") Integer postId,
                                   @RequestParam(value = "location", required = true) List<Double> location,
                                   @RequestParam(value = "zoneCode", required = true) Integer zoneCode) {
        if (cbFactory.create("events").run(
                () -> sensoresFeignClient.posteExiste(postId),
                this::errorExistsPoste)) {
            if (eventsService.crearEventoPoste(postId, location, zoneCode)) return "Evento creado correctamente";
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error en la creación del evento");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El poste: " + postId + " no existe");
    }


    @DeleteMapping("/eliminar/usuario/{username}")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarEventoUsuario(@PathVariable("username") String username) {
        eventsService.deleteAll();
        String id = userEventRepository.findByUserId(eventsService.obtenerIdUsuario(username)).getId();
        eventsService.deleteUser(id);
    }

    @DeleteMapping("/eliminar/all")
    @ResponseStatus(HttpStatus.OK)
    public void eliminarALl() {
        eventsService.deleteAll();
    }


    //  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

    private Boolean errorExistsUsername(Throwable e) {
        log.info("Error creacion zona: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio usuarios no esta disponible");
    }

    private Boolean errorExistsPoste(Throwable e) {
        log.info("Error creacion zona: " + e.getMessage());
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Servicio Sensores no esta disponible");
    }

}
