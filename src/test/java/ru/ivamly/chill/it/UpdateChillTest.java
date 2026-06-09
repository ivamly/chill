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
import ru.ivamly.chill.dto.UpdateChillRq;
import ru.ivamly.chill.dto.UpdateChillRs;
import ru.ivamly.chill.entity.Chill;
import ru.ivamly.chill.entity.enums.ChillType;
import ru.ivamly.chill.repository.ChillRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Обновить chill")
public class UpdateChillTest extends BaseIntegrationTest {

    @Autowired
    private ChillRepository chillRepository;

    static Stream<Arguments> provideInvalidRequests() {
        return Stream.of(
                Arguments.of(new UpdateChillRq(
                        UUID.randomUUID(),
                        ChillType.OFF,
                        null,
                        LocalDate.now(),
                        LocalDate.now().minusDays(1L)
                )),
                Arguments.of(new UpdateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        LocalDate.now(),
                        LocalDate.now().minusDays(1L)
                )),
                Arguments.of(new UpdateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        null,
                        LocalDate.now().plusDays(1L)
                )),
                Arguments.of(new UpdateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        null,
                        LocalDate.now(),
                        null
                )),
                Arguments.of(new UpdateChillRq(
                        UUID.randomUUID(),
                        ChillType.SICK,
                        "",
                        LocalDate.now(),
                        LocalDate.now().plusDays(1)
                ))
        );
    }

    @Test
    @SneakyThrows
    @DisplayName("Обновление chill")
    void shouldUpdateChill() {
        // given
        Chill chill = new Chill();
        chill.setUserId(UUID.randomUUID());
        chill.setType(ChillType.SICK);
        chill.setStartDate(LocalDate.now().minusDays(10));
        chill.setEndDate(LocalDate.now());
        Chill savedChill = chillRepository.save(chill);

        UpdateChillRq request = new UpdateChillRq(
                savedChill.getUserId(),
                ChillType.SICK,
                "some comment",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(5)
        );

        // when
        ResultActions resultAction = mockMvc.perform(put("/api/1/chills/{id}", savedChill.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isOk());
        UpdateChillRs response = getResponse(resultAction, UpdateChillRs.class);
        Chill updatedChill = chillRepository.findById(response.id()).orElseThrow();
        assertThat(response.id())
                .isEqualTo(updatedChill.getId())
                .isEqualTo(savedChill.getId());
        assertThat(response.userId())
                .isEqualTo(updatedChill.getUserId())
                .isEqualTo(request.userId());
        assertThat(response.type())
                .isEqualTo(updatedChill.getType())
                .isEqualTo(request.type());
        assertThat(response.comment())
                .isEqualTo(request.comment());
        assertThat(response.startDate())
                .isEqualTo(updatedChill.getStartDate())
                .isEqualTo(request.startDate());
        assertThat(response.endDate())
                .isEqualTo(updatedChill.getEndDate())
                .isEqualTo(request.endDate());
    }

    @Test
    @SneakyThrows
    @DisplayName("Ошибка обновления при пересечении с существующим chill")
    void shouldReturnConflict() {
        // given
        UUID userId = UUID.randomUUID();

        Chill existingChill = new Chill();
        existingChill.setUserId(userId);
        existingChill.setType(ChillType.SICK);
        existingChill.setStartDate(LocalDate.now().plusDays(1));
        existingChill.setEndDate(LocalDate.now().plusDays(11));
        chillRepository.save(existingChill);

        Chill chillToUpdate = new Chill();
        chillToUpdate.setUserId(userId);
        chillToUpdate.setType(ChillType.SICK);
        chillToUpdate.setStartDate(LocalDate.now().minusDays(10));
        chillToUpdate.setEndDate(LocalDate.now());
        Chill savedChillToUpdate = chillRepository.save(chillToUpdate);

        UpdateChillRq request = new UpdateChillRq(
                userId,
                ChillType.SICK,
                null,
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(1)
        );

        // when
        ResultActions resultAction = mockMvc.perform(put("/api/1/chills/{id}", savedChillToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isConflict());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isNotBlank();
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

    @Test
    @SneakyThrows
    @DisplayName("Обновление несуществующего chill")
    void shouldReturnNotFound() {
        // given
        UpdateChillRq request = new UpdateChillRq(
                UUID.randomUUID(),
                ChillType.SICK,
                null,
                LocalDate.now(),
                LocalDate.now().plusDays(5)
        );

        // when
        ResultActions resultAction = mockMvc.perform(put("/api/1/chills/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isNotFound());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isEqualTo("Not Found");
    }

    @SneakyThrows
    @ParameterizedTest
    @MethodSource("provideInvalidRequests")
    @DisplayName("Ошибка валидации")
    void shouldReturnBadRequest(UpdateChillRq request) {
        // when
        ResultActions resultAction = mockMvc.perform(put("/api/1/chills/{id}", UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content(getContent(request)));

        // then
        resultAction.andExpect(status().isBadRequest());
        ProblemDetail response = getResponse(resultAction, ProblemDetail.class);
        assertThat(response.getTitle())
                .isEqualTo("Bad Request");
    }
}
