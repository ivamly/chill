package ru.ivamly.chill.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.AbstractContextSource;
import org.springframework.security.authentication.AuthenticationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.security.DatabaseLdapAuthoritiesPopulator;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final DatabaseLdapAuthoritiesPopulator databaseLdapAuthoritiesPopulator;
    private final ApplicationEventPublisher eventPublisher;
    private final AbstractContextSource contextSource;
    private final HttpSecurity http;

    @Value("${ldap.userDnPattern}")
    private String userDnPattern;

    @Bean
    public SecurityFilterChain securityFilterChain () {
        return http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.successHandler(
                    (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_OK)
                ).failureHandler(
                    (request, response, authentication) -> response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
                )
            )
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
		authenticator.setUserDnPatterns(new String[] {userDnPattern});

        LdapAuthenticationProvider ldapProvider = 
                new LdapAuthenticationProvider(authenticator, databaseLdapAuthoritiesPopulator);

        ProviderManager providerManager = new ProviderManager(ldapProvider);

        AuthenticationEventPublisher defaultAuthenticationEventPublisher = new DefaultAuthenticationEventPublisher(eventPublisher);
        providerManager.setAuthenticationEventPublisher(defaultAuthenticationEventPublisher);

        return providerManager;
    }
}
