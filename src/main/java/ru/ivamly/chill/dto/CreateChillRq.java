package ru.ivamly.chill.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import ru.ivamly.chill.entity.enums.ChillType;

import java.time.LocalDate;
import java.util.UUID;

public record CreateChillRq(
        @NotNull
        UUID userId, // TODO это поле будет браться из LDAP
        @NotNull
        ChillType type,
        @NotNull
        LocalDate startDate,
        @NotNull
        LocalDate endDate
) {
    @AssertTrue
    public boolean isStartBeforeOrEqualEnd() {
        return !startDate.isAfter(endDate);
    }
}
