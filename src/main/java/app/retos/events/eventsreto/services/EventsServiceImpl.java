package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.clients.UsersFeignClient;
import app.retos.events.eventsreto.clients.ZonasFeignClient;
import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.models.Video;
import app.retos.events.eventsreto.repository.AudioRepository;
import app.retos.events.eventsreto.repository.EventRepository;
import app.retos.events.eventsreto.repository.PhotoRepository;
import app.retos.events.eventsreto.repository.VideoRepository;
import app.retos.events.eventsreto.requests.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    AudioRepository audioRepository;

    @Override
    public Boolean crearEventoUsuario(String username, UserEvent userEvent) {
        List<Double> location = userEvent.getLocation();
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
        if(userEvent.getComment() != null)
            events.setComment(userEvent.getComment());
        if(userEvent.getEventDescription() != null)
            events.setEventDescription(userEvent.getEventDescription());
        events.setPhotos(new ArrayList<>());
        events.setVideos(new ArrayList<>());
        events.setAudios(new ArrayList<>());
        try {
            eventRepository.save(events);
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
    public boolean guardarImagenes(String id, List<MultipartFile> imagenes) {
        try{
            imagenes.forEach(i -> {
                Photo photo = new Photo();
                photo.setEventId(id);
                photo.setName(i.getOriginalFilename());
                photo.setSize(i.getSize());
                try {
                    photo.setContent(new Binary(i.getBytes()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                photo.setContentType(i.getContentType());
                photo.setCreatedTime(new Date());
                String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                photo.setSuffix(suffix);
                photo.setImage(Base64.getEncoder().encodeToString(photo.getContent().getData()));
                photoRepository.save(photo);
            });
            Optional<Events> events = eventRepository.findById(id);
            if(events.isPresent()) {
                Events e = events.get();
                e.setPhotos(photoRepository.findImageById(id, Photo.class));
                eventRepository.save(e);
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarVideos(String id, List<MultipartFile> videos) {
        try{
            videos.forEach(i -> {
                Video video = new Video();
                video.setEventId(id);
                video.setName(i.getOriginalFilename());
                video.setSize(i.getSize());
                try {
                    video.setContent(new Binary(i.getBytes()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                video.setContentType(i.getContentType());
                video.setCreatedTime(new Date());
                String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                video.setSuffix(suffix);
                try {
                    video.setStream(i.getInputStream());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                videoRepository.save(video);
            });
            Optional<Events> events = eventRepository.findById(id);
            if(events.isPresent()) {
                Events e = events.get();
                e.setVideos(videoRepository.findByEventId(id));
                eventRepository.save(e);
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarAudios(String id, List<MultipartFile> audios) {
        try{
            audios.forEach(i -> {
                Photo photo = new Photo();
                photo.setEventId(id);
                photo.setName(i.getOriginalFilename());
                photo.setSize(i.getSize());
                try {
                    photo.setContent(new Binary(i.getBytes()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                photo.setContentType(i.getContentType());
                photo.setCreatedTime(new Date());
                String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                photo.setSuffix(suffix);
                photo.setImage(Base64.getEncoder().encodeToString(photo.getContent().getData()));
                photoRepository.save(photo);
            });
            Optional<Events> events = eventRepository.findById(id);
            if(events.isPresent()) {
                Events e = events.get();
                e.setAudios(audioRepository.findByEventId(id));
                eventRepository.save(e);
            }
            return true;
        }catch (Exception e) {
            e.printStackTrace();
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
