package app.retos.events.eventsreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "audios")
@Data
@NoArgsConstructor
public class Audio {

    @Id
    @JsonIgnore
    private String id;

    private Integer eventId;
    private String title;
    private String name; // file name
    private Date createdtime; // upload time
    private Binary content; // file content
    private String contentType; // file type
    private long size; // file size
    private String suffix;
    private Binary image;

}
