package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;


@Data
public class Film {
    private Long id;
    @NotBlank(message = "Название не должно быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    @NotBlank(message = "Описание не должно быть пустым")
    private String description;
    @NotNull(message = "Дата релиза не может пустой")
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность должна быть положительной")
    private int duration;
    private Set<Genre> genres = new HashSet<>();
    private Mpa mpa;
    private Set<Long> likes = new HashSet<>();
}
