package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.ResultActions;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
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
        chill.setStartDate(LocalDate.now().plusDays(1));
        chill.setEndDate(LocalDate.now().plusDays(10));
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

    @Test
    @SneakyThrows
    @DisplayName("Ошибка удаления начавшегося chill")
    void shouldReturnConflictWhenChillStarted() {
        // given
        Chill chill = new Chill();
        chill.setUserId(UUID.randomUUID());
        chill.setType(ChillType.OFF);
        chill.setStartDate(LocalDate.now());
        chill.setEndDate(LocalDate.now().plusDays(1));
        Chill savedChill = chillRepository.save(chill);

        // when
        ResultActions resultAction = mockMvc.perform(delete("/api/1/chills/{id}", savedChill.getId()));

        // then
        resultAction.andExpect(status().isConflict());
        assertThat(chillRepository.findById(savedChill.getId())).isPresent();
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isNotBlank();
        Map<String, Object> properties = response.getProperties();
        assertThat(properties)
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .isEqualTo(
                        Map.of(
                                "userId", chill.getUserId().toString(),
                                "chillStartDate", chill.getStartDate().toString()
                        )
                );
        assertThat(Instant.parse((String) response.getProperties().get("timestamp")))
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }
}
