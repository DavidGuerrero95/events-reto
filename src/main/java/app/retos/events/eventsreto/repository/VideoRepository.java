package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Video;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VideoRepository extends MongoRepository<Video, String> {
    
    List<Video> findByEventId(String id);

    void deleteByEventId(String id);
    
}
