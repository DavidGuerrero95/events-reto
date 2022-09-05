package app.retos.events.eventsreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Document(collection = "events")
@Data
@NoArgsConstructor
public class Events {

    @Id
    @JsonIgnore
    private String id;

    private Integer type;
    private String date;
    private String time;
    private String eventDescription;

    @NotEmpty(message = "locacion no puedde esta vacia")
    @Size(min=2,max = 2, message = "Debe tener dos valores")
    private List<Double> location;

    private Integer status;
    private String comment;
    private Integer zoneCode;

    @JsonIgnore
    private String historicalId;

}
