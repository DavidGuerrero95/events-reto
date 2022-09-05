package app.retos.events.eventsreto.services;

import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.models.Video;
import app.retos.events.eventsreto.repository.PhotoRepository;
import app.retos.events.eventsreto.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @Override
    public boolean guardarImagenes(String id, List<MultipartFile> imagenes) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarVideos(String id, List<MultipartFile> videos) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean guardarAudios(String id, List<MultipartFile> audios) {
        try {
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
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
