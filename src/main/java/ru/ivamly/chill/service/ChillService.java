package ru.ivamly.chill.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.repository.ChillRepository;

@Service
@RequiredArgsConstructor
public class ChillService {

    private final ChillRepository chillRepository;

    // TODO разобраться с транзакцией
    public Chill create(Chill chill) {
        if (chillRepository.existsOverlappingChill(chill.getUserId(), chill.getStartDate(), chill.getEndDate())) {
            throw new IllegalStateException("Заявка пересекается с уже существуюшей");
        }
        return chillRepository.save(chill);
    }
}
