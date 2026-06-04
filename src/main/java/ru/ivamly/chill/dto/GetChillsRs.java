package ru.ivamly.chill.dto;

import java.util.Collection;

public record GetChillsRs(
        Collection<ChillInfo> chills
) {
}
