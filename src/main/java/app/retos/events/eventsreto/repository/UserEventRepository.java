package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.Events;
import app.retos.events.eventsreto.models.UserEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import java.util.List;

public interface UserEventRepository extends MongoRepository<UserEvent, String> {

    @RestResource(path = "find-userId")
    UserEvent findByUserId(@Param("userId") String userId);

    @RestResource(path = "exists-userId")
    Boolean existsByUserId(@Param("userId") String userId);

}
