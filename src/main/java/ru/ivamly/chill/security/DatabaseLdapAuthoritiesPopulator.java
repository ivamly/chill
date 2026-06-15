package ru.ivamly.chill.security;

import java.util.Collection;
import java.util.Set;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.service.UserService;

@Component
@RequiredArgsConstructor
public class DatabaseLdapAuthoritiesPopulator implements LdapAuthoritiesPopulator {

    private final UserService userService;

    @Override
    public Collection<? extends GrantedAuthority> getGrantedAuthorities(
        DirContextOperations userData, String username
    ) {
        return userService.findByName(username)
                .map(User::getAuthorities)
                .orElse(Set.of());
    }
}
