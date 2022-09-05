package app.retos.events.eventsreto.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "zonas-reto")
public interface ZonasFeignClient {

    @GetMapping("/zonas/events/crear/")
    Integer obtainZonesEvents(@RequestParam("idEvents") String idEvents,
                              @RequestParam("location") List<Double> location);

    @GetMapping("/zonas/events/actualizar/")
    Integer obtainZonesEventsManyTimes(@RequestParam("idEvents") String idEvents,
                                       @RequestParam("location") List<Double> location,
                                       @RequestParam("zoneCode") Integer zoneCode);
}
