package ru.ivamly.chill.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.exception.handler.OverlappingChillException;
import ru.ivamly.chill.repository.ChillRepository;

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

    public Chill get(UUID id) {
        return chillRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
    }
}
