package ru.ivamly.chill.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import ru.ivamly.chill.entity.enums.ChillType;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Entity
public class Chill {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private ChillType type;
    private LocalDate startDate;
    private LocalDate endDate;
}
