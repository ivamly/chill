package ru.ivamly.chill.dto;

import ru.ivamly.chill.entity.enums.ChillType;

import java.time.LocalDate;
import java.util.UUID;

public record UpdateChillRs(
        UUID id,
        UUID userId,
        ChillType type,
        LocalDate startDate,
        LocalDate endDate
) {
}
