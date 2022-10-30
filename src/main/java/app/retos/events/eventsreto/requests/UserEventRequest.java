package app.retos.events.eventsreto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Data
@NoArgsConstructor
public class UserEventRequest {

    @NotEmpty(message = "ubicacion no puedde esta vacia")
    @Size(min=2,max = 2, message = "Debe tener dos valores (Lat, Lon)")
    private List<Double> location;

    @Min(1)
    @Max(10)
    @NotNull(message = "no puede estar vacio")
    private Integer typeEmergency;

    private String comment;

}
