package app.retos.events.eventsreto.controllers;

import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.repository.*;
import app.retos.events.eventsreto.response.FileEventResponse;
import app.retos.events.eventsreto.services.IEventsService;
import app.retos.events.eventsreto.services.IFilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/files")
public class FilesController {

    @Autowired
    EventRepository eventRepository;

    @Autowired
    UserEventRepository userEventRepository;

    @Autowired
    PostEventRepository postEventRepository;

    @Autowired
    IEventsService eventsService;

    @Autowired
    IFilesService filesService;

    @Autowired
    AudioRepository audioRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    VideoRepository videoRepository;

    @GetMapping("/obtener/{username}")
    @ResponseStatus(code = HttpStatus.OK)
    public FileEventResponse obtenerFiles(@PathVariable("id") String id) {
        return filesService.obtenerArchivos(id);
    }

    @GetMapping("/obtener/all/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public List<Photo> obtenerAll(@PathVariable("id") String id) {
        return photoRepository.findByEventId(id);
    }

    @PostMapping(path = "/anexar/usuarios/{username}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String agregarArchivosUsuario(@PathVariable("username") String username,
                                         @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
                                         @RequestPart(value = "videos", required = false) List<MultipartFile> videos,
                                         @RequestPart(value = "audios", required = false) List<MultipartFile> audios) throws Exception {
        if (eventsService.existeUsuario(username)) {
            String id = userEventRepository.findByUserId(eventsService.obtenerIdUsuario(username)).getId();
            filesService.guardarImagenes(id, imagenes);
            filesService.guardarVideos(id, videos);
            filesService.guardarAudios(id, audios);
            return "Archivos agregados correctamente";
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El usuario: " + username + " no existe");
    }

    @PostMapping("/anexar/poste/{postId}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String agregarArchivosPoste(@PathVariable("postId") Integer postId,
                                       @RequestParam("imagenes") List<MultipartFile> imagenes,
                                       @RequestParam("videos") List<MultipartFile> videos,
                                       @RequestParam("audios") List<MultipartFile> audios, MultipartRequest request) {
        String id = postEventRepository.findByPostId(postId).getId();

        if (imagenes != null)
            filesService.guardarImagenes(id, imagenes);
        if (imagenes != null)
            filesService.guardarVideos(id, videos);
        if (imagenes != null)
            filesService.guardarAudios(id, audios);
        return "Archivos agregados correctamente";
    }
}
