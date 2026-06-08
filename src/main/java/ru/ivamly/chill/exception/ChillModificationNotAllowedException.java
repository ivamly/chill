package ru.ivamly.chill.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class ChillModificationNotAllowedException extends RuntimeException {
    private final UUID userId;
    private final LocalDate chillStartDate;
}
