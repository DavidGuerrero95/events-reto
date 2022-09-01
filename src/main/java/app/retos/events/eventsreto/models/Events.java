package app.retos.events.eventsreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "events")
@Data
@NoArgsConstructor
public class Events {

    @Id
    @JsonIgnore
    private String id;

    private String userId;
    private String postId;
    private Integer type;
    private String date;
    private String time;
    private List<Double> location;
    private Integer status;
    private String comment;
    private Integer zoneCode;
    private List<Photo> photos;
    private List<Video> videos;
    private List<Audio> audios;

}
