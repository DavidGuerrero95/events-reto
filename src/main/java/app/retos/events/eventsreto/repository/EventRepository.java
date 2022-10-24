package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Events;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface EventRepository extends MongoRepository<Events, String> {

    @RestResource(path = "find-zoneCode")
    List<Events> findByZone(@Param("zone") Integer zone);

    @RestResource(path = "find-status")
    List<Events> findByStatus(@Param("status") Integer status);

}
