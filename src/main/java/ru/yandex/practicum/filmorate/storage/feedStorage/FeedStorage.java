package ru.yandex.practicum.filmorate.storage.feedStorage;

import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

public interface FeedStorage {

    List<Feed> getEventFeed(int userId);

    void addEventFeed(int userId, int entityId, EventType eventType, Operation operation);

}
