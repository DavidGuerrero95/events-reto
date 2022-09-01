package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VideoRepository extends MongoRepository<Video, String> {
}
