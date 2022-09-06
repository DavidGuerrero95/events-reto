package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.response.FileEventResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IFilesService {


    boolean guardarImagenes(String id, List<MultipartFile> imagenes);

    boolean guardarVideos(String id, List<MultipartFile> videos);

    boolean guardarAudios(String id, List<MultipartFile> audios);

    FileEventResponse obtenerArchivos(String id);

    boolean sendFiles(Events events);
}
