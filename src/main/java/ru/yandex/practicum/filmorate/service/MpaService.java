package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpaStorage.MpaStorage;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> getMpa(){
        return mpaStorage.getMpa();
    }

    public Mpa getMpaToId(int id){
        return mpaStorage.getMpaToId(id);
    }
}
