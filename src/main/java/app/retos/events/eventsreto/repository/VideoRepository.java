package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Video;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface VideoRepository extends MongoRepository<Video, String> {

    @RestResource(path = "buscar Imagen id")
    List<Video> findByEventId(@Param("eventId") String eventId);

    Video findVideoById(String id, Class<Video> class1);

    void deleteByEventId(String id);

}
