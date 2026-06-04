package ru.ivamly.chill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.ivamly.chill.entity.Chill;

import java.time.LocalDate;
import java.util.Collection;
import java.util.UUID;

public interface ChillRepository extends JpaRepository<Chill, UUID> {

    @Query("""
            SELECT EXISTS (
            SELECT 1 FROM Chill c
            WHERE c.userId = :userId
            AND c.startDate <= :endDate
            AND c.endDate >= :startDate
            )
            """)
    boolean existsOverlappingChill(UUID userId, LocalDate startDate, LocalDate endDate);

    @Query("""
            SELECT c FROM Chill c
            WHERE c.userId = :userId
            AND c.startDate <= :end
            AND c.endDate >= :start
            """)
    Collection<Chill> findByUserIdAndDatesBetween(UUID userId, LocalDate start, LocalDate end);
}
