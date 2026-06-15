package ru.ivamly.chill.it;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import lombok.SneakyThrows;
import ru.ivamly.chill.config.MockMvcConfig;
import ru.ivamly.chill.entity.enums.Authority;

public class SecurityTest extends BaseIntegrationTest {

    @Test
    @SneakyThrows
    @DisplayName("Успешная авторизация и проверка прав пользоввателя")
    void shouldAssignAuthorityToUser() {

        // given
        var testAuthority = "\"" + Authority.MANAGER.name() + "\"";

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignAuthority")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testAuthority));

        // then
        perform.andExpect(status().is2xxSuccessful());
    }

    @Test
    @SneakyThrows
    @DisplayName("Ошибка при попытке вызвать апи без необходимых прав")
    void shouldThrowForbidden() {

        // given
        var testAuthority = "\"" + Authority.MANAGER.name() + "\"";

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignAuthority")
            .with(MockMvcConfig.replaceHttpBasic("user","user"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testAuthority));

        // then
        perform.andExpect(status().isForbidden());
    }

    @Test
    @SneakyThrows
    @DisplayName("Ошибка при попытке вызвать апи без аутентификации")
    void shouldThrowUnauthorized() {

        // given
        var testAuthority = Authority.MANAGER.name();

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignAuthority")
                .with(MockMvcConfig.replaceHttpBasic("unknown","unknown"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(testAuthority));
            
        // then
        perform.andExpect(status().isUnauthorized());
    }
}
