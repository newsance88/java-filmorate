package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.mapper.MapRowClass;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
@Slf4j
@RequiredArgsConstructor
@Component
public class MpaDbStorage implements MpaStorage{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, MapRowClass::mapRowToMpa);
    }

    @Override
    public Mpa getMpaById(Long id) {
        String sql = "SELECT * FROM mpa WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, MapRowClass::mapRowToMpa, id);
    }
}
