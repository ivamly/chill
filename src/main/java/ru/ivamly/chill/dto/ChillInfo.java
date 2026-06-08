package ru.ivamly.chill.dto;

import ru.ivamly.chill.entity.enums.ChillType;

import java.time.LocalDate;
import java.util.UUID;

public record ChillInfo(
        UUID id,
        UUID userId,
        ChillType type,
        String comment,
        LocalDate startDate,
        LocalDate endDate
) {
}
