package ru.ivamly.chill.it;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import lombok.SneakyThrows;
import ru.ivamly.chill.entity.enums.Authority;
import ru.ivamly.chill.service.UserService;

public class SecurityTest extends BaseIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setupSecurity() {
        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @SneakyThrows
    @DisplayName("Успешная авторизация и проверка прав пользоввателя")
    void shouldAssignAuthorityToUser() {

        // given
        var testAuthority = "\"" + Authority.MANAGER.name() + "\"";

        // when
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignAuthorityToUser")
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
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignAuthorityToUser")
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
        ResultActions perform = mockMvc.perform(post("/api/1/users/assignAuthorityToUser")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testAuthority));
            
        // then
        perform.andExpect(status().isUnauthorized());
    }
}
