package ru.ivamly.chill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.AbstractContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.security.AuthenticationSuccessHandler;
import ru.ivamly.chill.security.DatabaseLdapAuthoritiesPopulator;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DatabaseLdapAuthoritiesPopulator databaseLdapAuthoritiesPopulator;
    private final AuthenticationSuccessHandler authenticationSuccessHandler;
    private final AbstractContextSource contextSource;
    private final HttpSecurity http;

    @Value("${ldap.userDnPattern}")
    private String userDnPattern;

    @Bean
    public SecurityFilterChain securityFilterChain () {
        // TODO проверить корректность очередности фильтров в цепочке
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            // TODO ебануть редирект на страницу логина только при обращении с браузера
            .formLogin(form -> form.successHandler(authenticationSuccessHandler))
            // TODO добавить что-то взамен authenticationSuccessHandler, чтобы хавал при httpBasic
            .httpBasic(Customizer.withDefaults())
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
		authenticator.setUserDnPatterns(new String[] {userDnPattern});

        LdapAuthenticationProvider ldapProvider = 
                new LdapAuthenticationProvider(authenticator, databaseLdapAuthoritiesPopulator);

        return new ProviderManager(ldapProvider);
    }
}
