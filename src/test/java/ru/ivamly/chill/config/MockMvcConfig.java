package ru.ivamly.chill.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.MockMvcBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@TestConfiguration
public class MockMvcConfig {

    @Bean
    public MockMvcBuilderCustomizer securityCustomizerWithAuth() {
        return builder -> builder.defaultRequest(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/")
                .with(SecurityMockMvcRequestPostProcessors.httpBasic("admin", "admin"))
        );
    }

    public static RequestPostProcessor replaceHttpBasic(String username, String password) {
        return request -> {
            request.removeHeader("Authorization"); 
            return SecurityMockMvcRequestPostProcessors.httpBasic(username, password).postProcessRequest(request);
        }; 
    }   
}
