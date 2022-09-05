package app.retos.events.eventsreto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
public class UserEventRequest {

    @NotEmpty(message = "ubicacion no puedde esta vacia")
    @Size(min=2,max = 2, message = "Debe tener dos valores (Lat, Lon)")
    private List<Double> location;

    private String eventDescription;

    private String comment;

}
