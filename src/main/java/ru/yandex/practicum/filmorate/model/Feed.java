package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Feed {

    int eventId;
    int userId;
    int entityId;
    String eventType;
    String operation;
    Long timestamp;

}
