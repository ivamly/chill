package ru.ivamly.chill.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.ivamly.chill.entity.Chill;

import java.time.LocalDate;
import java.util.UUID;

public interface ChillRepository extends JpaRepository<Chill, UUID> {

    @Query("""
            SELECT EXISTS (
            SELECT 1 FROM Chill c
            WHERE c.userId = :userId
            AND c.startDate <= :newEndDate
            AND c.endDate >= :newStartDate
            )
            """)
    boolean existsOverlappingChill(@Param("userId") UUID userId,
                                   @Param("newStartDate") LocalDate newStartDate,
                                   @Param("newEndDate") LocalDate newEndDate);
}
