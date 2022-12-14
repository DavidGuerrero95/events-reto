package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.clients.HistoricalFeignClient;
import app.retos.events.eventsreto.models.Audio;
import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.models.Video;
import app.retos.events.eventsreto.repository.AudioRepository;
import app.retos.events.eventsreto.repository.PhotoRepository;
import app.retos.events.eventsreto.repository.UserEventRepository;
import app.retos.events.eventsreto.repository.VideoRepository;
import app.retos.events.eventsreto.response.FileEventResponse;
import com.mongodb.MongoException;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.events.Event;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FilesServiceImpl implements IFilesService {

    @Autowired
    private CircuitBreakerFactory cbFactory;
    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    AudioRepository audioRepository;

    @Autowired
    HistoricalFeignClient historicalFeignClient;

    @Override
    public boolean guardarImagenes(String id, List<MultipartFile> imagenes) {
        try {
            for (MultipartFile i : imagenes) {
                Photo photo = new Photo();
                photo.setEventId(id);
                try {
                    photo.setName(i.getOriginalFilename());
                    photo.setCreatedtime(new Date());
                    photo.setContent(new Binary(i.getBytes()));
                    photo.setContentType(i.getContentType());
                    photo.setSize(i.getSize());
                    String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                    photo.setSuffix(suffix);
                    photo.setImage(Base64.getEncoder().encodeToString(photo.getContent().getData()));
                    log.info("Guardo foto");
                } catch (IOException e) {
                    log.error("ERROR: " + e.getMessage() + " OTRO:" + e.getLocalizedMessage());
                }
                photoRepository.save(photo);
            }
            return true;
        } catch (Exception e) {
            log.error("ERROR 2: " + e.getMessage() + " OTRO:" + e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean guardarVideos(String id, List<MultipartFile> videos) {
        try {
            videos.forEach(i -> {
                Video video = new Video();
                video.setEventId(id);
                try {
                    video.setName(i.getOriginalFilename());
                    video.setCreatedTime(new Date());
                    video.setSize(i.getSize());
                    video.setContent(new Binary(i.getBytes()));
                    video.setContentType(i.getContentType());
                    String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                    video.setSuffix(suffix);
                    video.setStream(Base64.getEncoder().encodeToString(video.getContent().getData()));
                    try {
                        videoRepository.save(video);
                    }catch (MongoException mongoException){
                        log.info("Error: "+mongoException.getMessage()+"Error 2: "+mongoException.getLocalizedMessage());
                    }
                    log.info("Guardo video");
                } catch (IOException e) {
                    log.error("ERROR: " + e.getMessage() + " OTRO:" + e.getLocalizedMessage());
                }
            });
            return true;
        } catch (Exception e) {
            log.error("ERROR 2: " + e.getMessage() + " OTRO:" + e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public boolean guardarAudios(String id, List<MultipartFile> audios) {
        try {
            audios.forEach(i -> {
                Audio audio = new Audio();
                audio.setEventId(id);
                try {
                    audio.setName(i.getOriginalFilename());
                    audio.setCreatedTime(new Date());
                    audio.setContent(new Binary(i.getBytes()));
                    audio.setContentType(i.getContentType());
                    audio.setSize(i.getSize());
                    String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                    audio.setSuffix(suffix);
                    audio.setImage(Base64.getEncoder().encodeToString(audio.getContent().getData()));
                    audioRepository.save(audio);
                    log.info("Guardo audio");
                } catch (IOException e) {
                    log.error("ERROR: " + e.getMessage() + " OTRO:" + e.getLocalizedMessage());
                }
            });
            return true;
        } catch (Exception e) {
            log.error("ERROR 2: " + e.getMessage() + " OTRO:" + e.getLocalizedMessage());
            return false;
        }
    }

    @Override
    public FileEventResponse obtenerArchivos(String id) {
        List<Photo> photos = photoRepository.findByEventId(id);
        List<Video> videos = videoRepository.findByEventId(id);
        List<Audio> audio = audioRepository.findByEventId(id);
        List<String> photosSend = new ArrayList<>();
        List<String> videosSend = new ArrayList<>();
        List<String> audiosSend = new ArrayList<>();
        photos.forEach(x -> {
            photosSend.add(x.getImage());
        });
        videos.forEach(x -> {
            videosSend.add(x.getStream());
        });
        audio.forEach(x -> {
            audiosSend.add(x.getImage());
        });

        return new FileEventResponse(photosSend, videosSend, audiosSend);
    }

    @Override
    public boolean sendFiles(Events events) {
        String id = events.getId();
        FileEventResponse fileEventResponse = obtenerArchivos(id);
        String historicalId = events.getHistoricalId();
        if(!historicalId.equals("-1")) {
            return (cbFactory.create("events").run(
                    () -> historicalFeignClient.guardarFiles(historicalId,fileEventResponse),
                    this::errorObtenerHistorical));
        }
        return false;
    }

    //  ****************************	FUNCIONES TOLERANCIA A FALLOS	***********************************  //

    private boolean errorObtenerHistorical(Throwable e) {
        log.info("Error al crear historico: " + e.getMessage());
        return false;
    }


}
