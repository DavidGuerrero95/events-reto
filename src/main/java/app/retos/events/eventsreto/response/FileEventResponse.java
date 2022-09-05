package app.retos.events.eventsreto.response;

import app.retos.events.eventsreto.models.Audio;
import app.retos.events.eventsreto.models.Photo;
import app.retos.events.eventsreto.models.Video;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEventResponse {

    private List<String> photos;
    private List<Video> videos;
    private List<Audio> audios;

}
