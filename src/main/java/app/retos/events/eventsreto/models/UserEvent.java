package app.retos.events.eventsreto.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "events")
@Data
@NoArgsConstructor
public class UserEvent extends Events{

    private String userId;

}
