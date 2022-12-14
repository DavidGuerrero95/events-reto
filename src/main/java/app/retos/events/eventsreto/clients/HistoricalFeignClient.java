package app.retos.events.eventsreto.clients;

import app.retos.events.eventsreto.response.FileEventResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "historical-retos")
public interface HistoricalFeignClient {

    @PostMapping("/historico/crear")
    String crearHistorico(@RequestParam("eventId") String eventId, @RequestParam("type") Integer type,
                          @RequestParam("date") String date, @RequestParam("time") String time,
                          @RequestParam("typeEmergency") Integer typeEmergency,
                          @RequestParam("location") List<Double> location, @RequestParam("status") Integer status,
                          @RequestParam("comment") String comment, @RequestParam("zoneCode") Integer zoneCode);

    @PutMapping("/historico/files/{historicalId}")
    public Boolean guardarFiles(@PathVariable("historicalId") String historicalId, @RequestBody FileEventResponse fileEventResponse);
}
