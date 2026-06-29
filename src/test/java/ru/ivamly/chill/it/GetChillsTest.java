package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import ru.ivamly.chill.dto.GetChillsRs;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Получение chills")
class GetChillsTest extends BaseIntegrationTest {

    @Autowired
    private ChillRepository chillRepository;

    @Test
    @SneakyThrows
    @DisplayName("Получить chills")
    void shouldReturnChills() {
        UUID userId = UUID.randomUUID();

        List<Chill> chills = new ArrayList<>();
        Chill earlyChill = new Chill();
        earlyChill.setUserId(userId);
        earlyChill.setType(ChillType.SICK);
        earlyChill.setStartDate(LocalDate.now().minusMonths(2).with(TemporalAdjusters.firstDayOfMonth()));
        earlyChill.setEndDate(LocalDate.now().minusMonths(1).with(TemporalAdjusters.firstDayOfMonth()));
        chills.add(earlyChill);

        Chill lateChill = new Chill();
        lateChill.setUserId(userId);
        lateChill.setType(ChillType.OFF);
        lateChill.setStartDate(LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth()));
        lateChill.setEndDate(LocalDate.now().plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
        chills.add(lateChill);

        List<Chill> savedChills = chillRepository.saveAll(chills);

        // when
        // TODO вынести настройку mockMvc в конфигурации
        ResultActions resultAction = mockMvc.perform(get("/api/1/users/{id}/chills", userId)
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin")));

        // then
        resultAction.andExpect(status().isOk());
        GetChillsRs response = getResponse(resultAction, GetChillsRs.class);
        assertThat(response.chills())
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(savedChills);
    }

    @Test
    @SneakyThrows
    @DisplayName("Получить chills при их отсутствии в необходимом периоде")
    void shouldReturnEmptyWhenChillsNotInRange() {
        UUID userId = UUID.randomUUID();

        Chill earlierChill = new Chill();
        earlierChill.setUserId(userId);
        earlierChill.setType(ChillType.SICK);
        earlierChill.setStartDate(LocalDate.now().minusMonths(2).with(TemporalAdjusters.firstDayOfMonth()));
        earlierChill.setEndDate(LocalDate.now().minusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
        chillRepository.save(earlierChill);

        Chill laterChill = new Chill();
        laterChill.setUserId(userId);
        laterChill.setType(ChillType.OFF);
        laterChill.setStartDate(LocalDate.now().plusMonths(2).with(TemporalAdjusters.firstDayOfMonth()));
        laterChill.setEndDate(LocalDate.now().plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
        chillRepository.save(laterChill);

        // when
        ResultActions resultAction = mockMvc.perform(get("/api/1/users/{id}/chills", userId));

        // then
        resultAction.andExpect(status().isOk());
        GetChillsRs response = getResponse(resultAction, GetChillsRs.class);
        assertThat(response.chills())
                .isEmpty();
    }

    @Test
    @SneakyThrows
    @DisplayName("Получить chills при их отсутствии")
    void shouldReturnEmptyWhenNoChills() {
        // when
        ResultActions resultAction = mockMvc.perform(get("/api/1/users/{id}/chills", UUID.randomUUID()));

        // then
        resultAction.andExpect(status().isOk());
        GetChillsRs response = getResponse(resultAction, GetChillsRs.class);
        assertThat(response.chills())
                .isEmpty();
    }
}
