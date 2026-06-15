package ru.ivamly.chill.it;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import lombok.SneakyThrows;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
// TODO переписать на миграции
@Sql("classpath:add-admin-to-users.sql")
abstract class BaseIntegrationTest { // TODO добавить конфигурацию api versioning

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    protected <T> String getContent(T value) {
        return jsonMapper.writeValueAsString(value);
    }

    @SneakyThrows
    protected <T> T getResponse(ResultActions resultAction, Class<T> type) {
        return jsonMapper.readValue(resultAction.andReturn().getResponse().getContentAsString(), type);
    }
}
