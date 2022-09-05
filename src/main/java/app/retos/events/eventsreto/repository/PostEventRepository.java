package app.retos.events.eventsreto.repository;

import app.retos.events.eventsreto.models.PostEvent;
import app.retos.events.eventsreto.models.UserEvent;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

public interface PostEventRepository extends MongoRepository<PostEvent, String> {

    @RestResource(path = "find-userId")
    PostEvent findByPostId(@Param("postId") Integer postId);

    @RestResource(path = "exists-userId")
    Boolean existsByPostId(@Param("postId") Integer postId);

}
