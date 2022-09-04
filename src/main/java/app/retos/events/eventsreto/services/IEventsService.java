package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.requests.UserEvent;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEventsService {

    Boolean crearEventoUsuario(String username, UserEvent userEvent);

    String obtenerIdUsuario(String username);

    boolean crearEventoPoste(Integer postId, List<Double> location, Integer zoneCode);

    boolean guardarImagenes(String id, List<MultipartFile> imagenes);

    boolean guardarVideos(String id, List<MultipartFile> videos);

    boolean guardarAudios(String id, List<MultipartFile> audios);
}
