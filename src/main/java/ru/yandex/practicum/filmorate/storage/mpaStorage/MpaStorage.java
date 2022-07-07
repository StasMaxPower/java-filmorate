package ru.yandex.practicum.filmorate.storage.mpaStorage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {

    public List<Mpa> getMpa();

    public Mpa getMpaToId(int id);

    public void checkId(int id);
}
