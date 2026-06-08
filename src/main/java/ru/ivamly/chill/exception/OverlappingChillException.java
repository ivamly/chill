package ru.ivamly.chill.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class OverlappingChillException extends RuntimeException {
    private final UUID userId;
    private final LocalDate startDate;
    private final LocalDate endDate;
}