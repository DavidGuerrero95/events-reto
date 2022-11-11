package app.retos.events.eventsreto.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "app-notificaciones")
public interface NotificationsFeignClient {

    @PostMapping("/notificaciones/eventos/")
    void enviarMensajeAlerta(@RequestParam("emails") List<String> emails, @RequestParam("name") String name,
                                    @RequestParam("typeEmergency") Integer typeEmergency);
}
