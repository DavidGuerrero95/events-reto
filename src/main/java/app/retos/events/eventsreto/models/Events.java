package app.retos.events.eventsreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "events")
@Data
@NoArgsConstructor
public class Events {

    @Id
    private String id;

    @Min(1)
    @Max(5)
    private Integer eventOrigin;
    private String date;
    private String time;

    @NotEmpty(message = "locacion no puedde esta vacia")
    @Size(min = 2, max = 2, message = "Debe tener dos valores")
    private List<Double> location;

    @Min(1)
    @Max(3)
    private Integer status;
    private String comment;
    private Integer zone;

    @Min(1)
    @Max(10)
    private Integer typeEmergency;

    @JsonIgnore
    private String historicalId;

}
