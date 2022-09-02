package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Events;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface EventRepository extends MongoRepository<Events, String> {

    @RestResource(path = "find-userId")
    Events findByUserId(@Param("userId") String userId);

    @RestResource(path = "find-postId")
    Events findByPostId(@Param("postId") Integer postId);

    @RestResource(path = "find-zoneCode")
    List<Events> findByZoneCode(@Param("zoneCode") Integer zoneCode);

    @RestResource(path = "find-status")
    List<Events> findByStatus(@Param("status") Integer status);

    @RestResource(path = "exists-userId")
    Boolean existsByUserId(@Param("userId") String userId);

    @RestResource(path = "exists-postId")
    Boolean existsByPostId(@Param("userId") Integer postId);

}
