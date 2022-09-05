package app.retos.events.eventsreto.Controllers;

import app.retos.events.eventsreto.models.Audio;
import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.models.Video;
import app.retos.events.eventsreto.repository.AudioRepository;
import app.retos.events.eventsreto.repository.EventRepository;
import app.retos.events.eventsreto.repository.PhotoRepository;
import app.retos.events.eventsreto.repository.VideoRepository;
import app.retos.events.eventsreto.response.FileEventResponse;
import app.retos.events.eventsreto.services.IEventsService;
import app.retos.events.eventsreto.services.IFilesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/files")
public class FilesController {

    @Autowired
    EventRepository eventRepository;

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

    @GetMapping("/obtener/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public FileEventResponse obtenerFiles(@PathVariable("id") String id){
        List<Photo> photos = photoRepository.findImageById(id, Photo.class);
        List<Video> videos = videoRepository.findByEventId(id, Video.class);
        List<Audio> audios = audioRepository.findByEventId(id, Audio.class);
        FileEventResponse fileEventResponse = new FileEventResponse(photos, videos, audios);
        return fileEventResponse;
    }

    @PostMapping("/anexar/usuarios/{username}")
    @ResponseStatus(code = HttpStatus.CREATED)
    public String agregarArchivosUsuario(@PathVariable("username") String username,
                                         @RequestParam("imagenes") List<MultipartFile> imagenes,
                                         @RequestParam("videos") List<MultipartFile> videos,
                                         @RequestParam("audios") List<MultipartFile> audios) {
        String id = eventRepository.findByUserId(eventsService.obtenerIdUsuario(username)).getId();

        if (imagenes != null)
            filesService.guardarImagenes(id, imagenes);
        if (imagenes != null)
            filesService.guardarVideos(id, videos);
        if (imagenes != null)
            filesService.guardarAudios(id, audios);
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
            filesService.guardarImagenes(id, imagenes);
        if (imagenes != null)
            filesService.guardarVideos(id, videos);
        if (imagenes != null)
            filesService.guardarAudios(id, audios);
        return "Archivos agregados correctamente";
    }
}
