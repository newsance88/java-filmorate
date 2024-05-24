package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Data
@RequiredArgsConstructor
public class User {
    private int id;
    @Email
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
}
