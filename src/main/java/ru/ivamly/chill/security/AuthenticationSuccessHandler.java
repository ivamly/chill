package ru.ivamly.chill.security;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.service.UserService;

@Component
@RequiredArgsConstructor
public class AuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;

    @Override
	public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) throws ServletException, IOException {
        String username = authentication.getName();
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
