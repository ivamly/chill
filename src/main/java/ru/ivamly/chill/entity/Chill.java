package ru.ivamly.chill.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
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
    private String comment;
    private LocalDate startDate;
    private LocalDate endDate;
    @Version
    private Long version;
}
