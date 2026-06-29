package ru.ivamly.chill.entity.enums;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

public enum Authority implements GrantedAuthority {
    MANAGER,
    ADMIN;

    @Override
    public @Nullable String getAuthority() {
        return name();
    }
}
