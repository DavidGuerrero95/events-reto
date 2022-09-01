package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Photo;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PhotoRepository extends MongoRepository<Photo, String> {
}
