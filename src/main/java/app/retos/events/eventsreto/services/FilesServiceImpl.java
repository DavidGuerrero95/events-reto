package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.models.Audio;
import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.models.Video;
import app.retos.events.eventsreto.repository.AudioRepository;
import app.retos.events.eventsreto.repository.PhotoRepository;
import app.retos.events.eventsreto.repository.VideoRepository;
import app.retos.events.eventsreto.response.FileEventResponse;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class FilesServiceImpl implements IFilesService {

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    VideoRepository videoRepository;

    @Autowired
    AudioRepository audioRepository;

    @Override
    public boolean guardarImagenes(String id, List<MultipartFile> imagenes) {
        final boolean[] flag = {true};
        try {
            imagenes.forEach(i -> {
                Photo photo = new Photo();
                photo.setEventId(id);
                try {
                    photo.setName(i.getOriginalFilename());
                    photo.setCreatedTime(new Date());
                    photo.setContent(new Binary(i.getBytes()));
                    photo.setContentType(i.getContentType());
                    photo.setSize(i.getSize());
                    String suffix = i.getOriginalFilename().substring(i.getOriginalFilename().lastIndexOf("."));
                    photo.setSuffix(suffix);
                    photo.setImage(Base64.getEncoder().encodeToString(photo.getContent().getData()));
                    photoRepository.save(photo);
                    flag[0] = true;
                } catch (IOException e) {
                    flag[0] = false;
                    throw new RuntimeException(e);
                }
            });
            return flag[0];
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarVideos(String id, List<MultipartFile> videos) {
        final boolean[] flag = {true};
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
                    video.setStream(i.getInputStream());
                    videoRepository.save(video);
                } catch (IOException e) {
                    flag[0] = false;
                    throw new RuntimeException(e);
                }
            });
            return flag[0];
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarAudios(String id, List<MultipartFile> audios) {
        final boolean[] flag = {true};
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
                    flag[0] = true;
                } catch (IOException e) {
                    flag[0] = false;
                    throw new RuntimeException(e);
                }
            });
            return flag[0];
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public FileEventResponse obtenerArchivos(String id) {
        List<Photo> photos = photoRepository.findByEventId(id);
        List<Photo> photosSend = new ArrayList<>();
        photos.forEach(x -> {
            photosSend.add(photoRepository.findImageById(x.getId(), Photo.class));
        });
        List<Video> videos = videoRepository.findByEventId(id);
        List<Video> videoSend = new ArrayList<>();
        videos.forEach(x -> {
            videoSend.add(videoRepository.findVideoById(id, Video.class));
        });
        List<Audio> audio = audioRepository.findByEventId(id);
        List<Audio> audioSent = new ArrayList<>();
        audio.forEach(x -> {
            audioSent.add(audioRepository.findAudioById(id, Audio.class));
        });
        return new FileEventResponse(photosSend, videoSend, audioSent);
    }

}
