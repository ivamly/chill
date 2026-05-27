package ru.ivamly.chill.it;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.ResultActions;
import ru.ivamly.chill.dto.CreateChillRq;
import ru.ivamly.chill.dto.CreateChillRs;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Создание заявки на отгул")
class CreateDayOffChillTest extends BaseIntegrationTest {

    @Autowired
    private ChillRepository chillRepository;

    static Stream<Arguments> provideOverlappingChills() {
        Chill earlierSickChill = new Chill();
        earlierSickChill.setUserId(UUID.randomUUID());
        earlierSickChill.setType(ChillType.SICK);
        earlierSickChill.setStartDate(LocalDate.of(2026, Month.APRIL, 30));
        earlierSickChill.setEndDate(LocalDate.of(2026, Month.MAY, 5));
        Chill laterSickChill = new Chill();
        laterSickChill.setUserId(UUID.randomUUID());
        laterSickChill.setType(ChillType.SICK);
        laterSickChill.setStartDate(LocalDate.of(2026, Month.MAY, 5));
        laterSickChill.setEndDate(LocalDate.of(2026, Month.MAY, 11));
        Chill middleSickChill = new Chill();
        middleSickChill.setUserId(UUID.randomUUID());
        middleSickChill.setType(ChillType.SICK);
        middleSickChill.setStartDate(LocalDate.of(2026, Month.MAY, 2));
        middleSickChill.setEndDate(LocalDate.of(2026, Month.MAY, 9));
        Chill sickChill = new Chill();
        sickChill.setUserId(UUID.randomUUID());
        sickChill.setType(ChillType.SICK);
        sickChill.setStartDate(LocalDate.of(2026, Month.MAY, 1));
        sickChill.setEndDate(LocalDate.of(2026, Month.MAY, 10));
        Chill dayOffChill = new Chill();
        dayOffChill.setUserId(UUID.randomUUID());
        dayOffChill.setType(ChillType.OFF);
        dayOffChill.setStartDate(LocalDate.of(2026, Month.MAY, 5));
        dayOffChill.setEndDate(LocalDate.of(2026, Month.MAY, 5));
        return Stream.of(
                Arguments.of(earlierSickChill),
                Arguments.of(laterSickChill),
                Arguments.of(middleSickChill),
                Arguments.of(sickChill),
                Arguments.of(dayOffChill)
        );
    }

    static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(
                        new CreateChillRq(
                                UUID.randomUUID(),
                                ChillType.OFF,
                                LocalDate.now(),
                                LocalDate.now().plusDays(1)
                        ),
                        new CreateChillRq(
                                UUID.randomUUID(),
                                ChillType.SICK,
                                LocalDate.now(),
                                LocalDate.now().minusDays(1)
                        )
                )
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("Успешное создание заявки на отгул")
    void shouldCreateDayOffChill() {
        // given
        CreateChillRq request = new CreateChillRq(
                UUID.randomUUID(),
                ChillType.OFF,
                LocalDate.of(2026, Month.MAY, 1),
                LocalDate.of(2026, Month.MAY, 1)
        );

        // when
        ResultActions resultAction = mockMvc.perform(post("/api/1/chill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isCreated());
        CreateChillRs response = getResponse(resultAction, CreateChillRs.class);
        assertThat(response.id()).isNotNull();
        Chill chill = chillRepository.findById(response.id()).orElseThrow();
        assertThat(response.userId())
                .isEqualTo(chill.getUserId())
                .isEqualTo(request.userId());
        assertThat(response.type())
                .isEqualTo(chill.getType())
                .isEqualTo(request.type());
        assertThat(response.startDate())
                .isEqualTo(chill.getStartDate())
                .isEqualTo(request.startDate());
        assertThat(response.endDate())
                .isEqualTo(chill.getEndDate())
                .isEqualTo(request.endDate());
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideOverlappingChills")
    @DisplayName("Ошибка создания при пересечении с существующим chill")
    void shouldReturnConflict(Chill chill) {
        // given
        Chill savedChill = chillRepository.save(chill);
        CreateChillRq request = new CreateChillRq(
                savedChill.getUserId(),
                ChillType.OFF,
                LocalDate.of(2026, Month.MAY, 5),
                LocalDate.of(2026, Month.MAY, 5)
        );

        // when
        ResultActions resultAction = mockMvc.perform(post("/api/1/chill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isConflict());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isEqualTo("Существует chill в выбранные даты");
        Map<String, Object> properties = response.getProperties();
        assertThat(properties)
                .usingRecursiveComparison()
                .ignoringFields("timestamp")
                .isEqualTo(
                        Map.of(
                                "userId", request.userId().toString(),
                                "startDate", request.startDate().toString(),
                                "endDate", request.endDate().toString()
                        )
                );
        assertThat(Instant.parse((String) response.getProperties().get("timestamp")))
                .isCloseTo(Instant.now(), within(1, ChronoUnit.SECONDS));
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRequests")
    @DisplayName("Ошибка валидации")
    void shouldReturnBadRequest(CreateChillRq request) {
        // when
        ResultActions resultAction = mockMvc.perform(post("/api/1/chill")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isBadRequest());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isEqualTo("Bad Request");
    }
}
