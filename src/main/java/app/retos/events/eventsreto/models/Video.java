package app.retos.events.eventsreto.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.InputStream;
import java.util.Date;

@Document(collection = "videos")
@Data
@NoArgsConstructor
public class Video {

    @Id
    @JsonIgnore
    private String id;

    private String eventId;
    private String name; // file name
    private Date createdTime; // upload time
    private Binary content; // file content
    private String contentType; // file type
    private long size; // file size
    private String suffix;
    private String stream;

}
