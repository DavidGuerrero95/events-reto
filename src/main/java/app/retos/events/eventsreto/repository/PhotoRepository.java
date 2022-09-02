package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PhotoRepository extends MongoRepository<Photo, String> {

    List<Photo> findImageById(String id, Class<Photo> class1);

    void deleteByEventId(String id);

}
