package app.retos.events.eventsreto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.InputStream;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileEventResponse {

    private List<String> photos;
    private List<String> videos;
    private List<String> audios;

}
