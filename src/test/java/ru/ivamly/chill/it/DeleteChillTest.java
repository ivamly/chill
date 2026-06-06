package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Удалить chill")
class DeleteChillTest extends BaseIntegrationTest {

    @Autowired
    private ChillRepository chillRepository;

    @Test
    @SneakyThrows
    @DisplayName("Удалить существующий chill")
    void shouldDeleteExistingChill() {
        // given
        Chill chill = new Chill();
        chill.setUserId(UUID.randomUUID());
        chill.setType(ChillType.OFF);
        chill.setStartDate(LocalDate.of(2026, Month.MAY, 5));
        chill.setEndDate(LocalDate.of(2026, Month.MAY, 5));
        Chill savedChill = chillRepository.save(chill);

        // when
        ResultActions resultAction = mockMvc.perform(delete("/api/1/chills/{id}", savedChill.getId()));

        // then
        resultAction.andExpect(status().isNoContent());
        assertThat(chillRepository.findById(savedChill.getId())).isEmpty();
    }

    @Test
    @SneakyThrows
    @DisplayName("Удалить несуществующйи chill")
    void shouldDeleteNonExistingChill() {
        // given
        UUID nonExistingId = UUID.randomUUID();

        // when
        ResultActions resultAction = mockMvc.perform(delete("/api/1/chills/{id}", nonExistingId));

        // then
        resultAction.andExpect(status().isNoContent());
        assertThat(chillRepository.findById(nonExistingId)).isEmpty();
    }
}
