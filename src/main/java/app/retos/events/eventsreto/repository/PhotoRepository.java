package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface PhotoRepository extends MongoRepository<Photo, String> {

    @RestResource(path = "buscar Imagen id")
    Photo findByEventId(@Param("eventId") String eventId);

    Photo findImageById(String id, Class<Photo> class1);

    void deleteByEventId(String id);

}
