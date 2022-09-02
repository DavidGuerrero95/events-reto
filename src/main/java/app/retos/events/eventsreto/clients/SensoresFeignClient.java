package app.retos.events.eventsreto.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "sensores-reto")
public interface SensoresFeignClient {


    @GetMapping("/posteinteligente/existe/{postId}")
    Boolean posteExiste(@PathVariable("postId") Integer postId);

}
