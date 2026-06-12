package ru.ivamly.chill.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.exception.ChillModificationNotAllowedException;
import ru.ivamly.chill.exception.OverlappingChillException;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collection;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChillService {

    private final ChillRepository chillRepository;

    @Transactional // TODO добавить constraint в PostgreSQL
    public Chill create(Chill chill) {
        if (chillRepository.existsOverlappingChill(chill.getUserId(), chill.getStartDate(), chill.getEndDate())) {
            throw new OverlappingChillException(chill.getUserId(), chill.getStartDate(), chill.getEndDate());
        }
        return chillRepository.save(chill);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public Chill update(UUID id, Chill chill) {
        Chill existingChill = chillRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        if (!existingChill.getStartDate().isAfter(LocalDate.now())) {
            throw new ChillModificationNotAllowedException(existingChill.getUserId(), existingChill.getStartDate());
        }
        if (chillRepository.existsOverlappingChillExcludingIds(chill.getUserId(), chill.getStartDate(), chill.getEndDate(), id)) {
            throw new OverlappingChillException(chill.getUserId(), chill.getStartDate(), chill.getEndDate());
        }
        existingChill.setType(chill.getType());
        existingChill.setComment(chill.getComment());
        existingChill.setStartDate(chill.getStartDate());
        existingChill.setEndDate(chill.getEndDate());
        return existingChill;
    }

    @Transactional(readOnly = true)
    public Chill get(UUID id) {
        return chillRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }

    @Transactional(readOnly = true)
    public Collection<Chill> findByUserId(UUID userId) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayPrevMonth = today.minusMonths(1)
                .with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayNextMonth = today.plusMonths(1)
                .with(TemporalAdjusters.lastDayOfMonth());
        return chillRepository.findByUserIdAndDatesBetween(userId, firstDayPrevMonth, lastDayNextMonth);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void delete(UUID id) {
        chillRepository.findById(id)
                .ifPresent(chill -> {
                    if (!chill.getStartDate().isAfter(LocalDate.now())) {
                        throw new ChillModificationNotAllowedException(chill.getUserId(), chill.getStartDate());
                    }
                    chillRepository.delete(chill);
                });
    }
}
