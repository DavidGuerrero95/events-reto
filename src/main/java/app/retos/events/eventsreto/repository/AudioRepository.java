package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Audio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AudioRepository extends MongoRepository<Audio, String> {
    List<Audio> findByEventId(String id);

    Audio findAudioById(String id, Class<Audio> class1);

    void deleteByEventId(String id);
}
