package app.retos.events.eventsreto.clients;

import app.retos.events.eventsreto.requests.Contacts;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "usuarios-reto")
public interface UsersFeignClient {

    @GetMapping("/usuarios/obtener/{username}")
    String obtenerId(@PathVariable("username") String username);

    @GetMapping("/usuarios/preguntar/usuarioExiste")
    Boolean preguntarUsuarioExiste(@RequestParam(value = "username") String username);

    @GetMapping("/contactos/listar/{username}")
    List<Contacts> listarContactos(@PathVariable("username") String username);

}
