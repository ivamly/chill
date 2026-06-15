package ru.ivamly.chill.security;

import java.util.Optional;
import java.util.Set;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.service.UserService;

@Component
@RequiredArgsConstructor
public class RegistrationListener  {

    private final UserService userService;

    @EventListener
    public void onSuccess(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName();
        Optional<User> user = userService.findByName(username);

        if (user.isPresent()) {
            return;
        }

        User newUser = new User();
        newUser.setName(username);
        newUser.setAuthorities(Set.of());
        userService.create(newUser);
    }
}
