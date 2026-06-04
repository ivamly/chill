package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.ResultActions;
import ru.ivamly.chill.dto.GetChillRs;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.LocalDate;
import java.time.Month;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Получить информацию про chill")
class GetChillTest extends BaseIntegrationTest {

    @Autowired
    private ChillRepository chillRepository;

    @Test
    @SneakyThrows
    @DisplayName("Получить информацию о больничном")
    void shouldReturnSickChill() {
        // given
        Chill dayOffChill = new Chill();
        dayOffChill.setUserId(UUID.randomUUID());
        dayOffChill.setType(ChillType.OFF);
        dayOffChill.setStartDate(LocalDate.of(2026, Month.MAY, 5));
        dayOffChill.setEndDate(LocalDate.of(2026, Month.MAY, 5));
        Chill savedChill = chillRepository.save(dayOffChill);

        // when
        ResultActions resultAction = mockMvc.perform(get("/api/1/chills/{id}", savedChill.getId()));

        // then
        resultAction.andExpect(status().isOk());
        GetChillRs response = getResponse(resultAction, GetChillRs.class);
        assertThat(response.id())
                .isEqualTo(savedChill.getId());
        assertThat(response.userId())
                .isEqualTo(savedChill.getUserId());
        assertThat(response.type())
                .isEqualTo(savedChill.getType());
        assertThat(response.startDate())
                .isEqualTo(savedChill.getStartDate());
        assertThat(response.endDate())
                .isEqualTo(savedChill.getEndDate());
    }

    @Test
    @SneakyThrows
    @DisplayName("Получить информацию об отгуле")
    void shouldReturnDayOffChill() {
        // given
        Chill sickChill = new Chill();
        sickChill.setUserId(UUID.randomUUID());
        sickChill.setType(ChillType.SICK);
        sickChill.setStartDate(LocalDate.of(2026, Month.MAY, 1));
        sickChill.setEndDate(LocalDate.of(2026, Month.MAY, 10));
        Chill savedChill = chillRepository.save(sickChill);

        // when
        ResultActions resultAction = mockMvc.perform(get("/api/1/chills/{id}", savedChill.getId()));

        // then
        resultAction.andExpect(status().isOk());
        GetChillRs response = getResponse(resultAction, GetChillRs.class);
        assertThat(response.id())
                .isEqualTo(savedChill.getId());
        assertThat(response.userId())
                .isEqualTo(savedChill.getUserId());
        assertThat(response.type())
                .isEqualTo(savedChill.getType());
        assertThat(response.startDate())
                .isEqualTo(savedChill.getStartDate());
        assertThat(response.endDate())
                .isEqualTo(savedChill.getEndDate());
    }

    @Test
    @SneakyThrows
    @DisplayName("Получить информацию о несуществующем chill")
    void shouldReturnNotFound() {
        // when
        ResultActions resultAction = mockMvc.perform(get("/api/1/chills/{id}", UUID.randomUUID()));

        // then
        resultAction.andExpect(status().isNotFound());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isEqualTo("Not Found");
    }
}
