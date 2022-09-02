package app.retos.events.eventsreto.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "usuarios-reto")
public interface UsersFeignClient {

    @GetMapping("/usuarios/obtener/{username}")
    String obtenerId(@PathVariable("username") String username);

    @GetMapping("/usuarios/existe/todos/{dato}")
    Boolean EmailUsernameUsuarioExiste(@PathVariable("dato") String dato);

}
