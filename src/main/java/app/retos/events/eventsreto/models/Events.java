package app.retos.events.eventsreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Document(collection = "events")
@Data
@NoArgsConstructor
public class Events {

    @Id
    @JsonIgnore
    private String id;

    private String userId;
    private Integer postId;
    private Integer type;
    private String date;
    private String time;

    @NotBlank(message = "Locacion no puede ser null")
    private List<Double> location;

    private Integer status;
    private String comment;
    private Integer zoneCode;
    private List<Photo> photos;
    private List<Video> videos;
    private List<Audio> audios;

    @JsonIgnore
    private String historicalId;

}
