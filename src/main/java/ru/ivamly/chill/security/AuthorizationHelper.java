package ru.ivamly.chill.security;

import java.util.Collection;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthorizationHelper {

    public static void updateCurrentAuthority(Collection<? extends GrantedAuthority> authorities) {
        Authentication newAuthentication = SecurityContextHolder.getContext().getAuthentication().toBuilder()
                .authorities(auth -> auth.retainAll(authorities))
                .build();

        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
    }
}
