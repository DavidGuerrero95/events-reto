package app.retos.events.eventsreto.Controllers;

import app.retos.events.eventsreto.clients.SensoresFeignClient;
import app.retos.events.eventsreto.clients.UsersFeignClient;
import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.repository.EventRepository;
import app.retos.events.eventsreto.requests.UserEvent;
import app.retos.events.eventsreto.services.IEventsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/eventos")
public class EventsController {
    @SuppressWarnings("rawtypes")
    @Autowired
    private CircuitBreakerFactory cbFactory;
    @Autowired
    EventRepository eventRepository;

    @Autowired
    IEventsService eventsService;

    @Autowired
    UsersFeignClient usersFeignClient;

    @Autowired
    SensoresFeignClient sensoresFeignClient;

    @GetMapping("/listar")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Events> listarEventos() {
        return eventRepository.findAll();
    }

    @GetMapping("/listar/usuario/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public Events listarEventosUsuario(@PathVariable("username") String username) {
        String id = eventsService.obtenerIdUsuario(username);
        return eventRepository.findByUserId(id);
    }

    @GetMapping("/listar/poste/{postId}")
    @ResponseStatus(code = HttpStatus.OK)
    public Events listarEventosPoste(@PathVariable("postId") Integer postId) {
        return eventRepository.findByPostId(postId);
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
                                  @RequestBody @Validated UserEvent userEvent) throws InstantiationException, IllegalAccessException {
        if (eventsService.existeUsuario(username)){
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

    @PostMapping("/anexar/usuarios/{username}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String agregarArchivosUsuario(@PathVariable("username") String username,
                                  @RequestParam("imagenes") List<MultipartFile> imagenes,
                                  @RequestParam("videos") List<MultipartFile> videos,
                                  @RequestParam("audios") List<MultipartFile> audios) {
        String id = eventRepository.findByUserId(eventsService.obtenerIdUsuario(username)).getId();

        if (imagenes != null)
            eventsService.guardarImagenes(id, imagenes);
        if (imagenes != null)
            eventsService.guardarVideos(id, videos);
        if (imagenes != null)
            eventsService.guardarAudios(id, audios);
        return "Archivos agregados correctamente";
    }

    @PostMapping("/anexar/poste/{postId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String agregarArchivosPoste(@PathVariable("postId") Integer postId,
                                  @RequestParam("imagenes") List<MultipartFile> imagenes,
                                  @RequestParam("videos") List<MultipartFile> videos,
                                  @RequestParam("audios") List<MultipartFile> audios) {
        String id = eventRepository.findByPostId(postId).getId();

        if (imagenes != null)
            eventsService.guardarImagenes(id, imagenes);
        if (imagenes != null)
            eventsService.guardarVideos(id, videos);
        if (imagenes != null)
            eventsService.guardarAudios(id, audios);
        return "Archivos agregados correctamente";
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
