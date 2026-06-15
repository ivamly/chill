package ru.ivamly.chill.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.entity.User;
import ru.ivamly.chill.entity.enums.Authority;
import ru.ivamly.chill.exception.UserExistException;
import ru.ivamly.chill.repository.UserRepository;
import ru.ivamly.chill.security.AuthorizationHelper;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        if (userRepository.existsByName(user.getName())) {
            throw new UserExistException(user.getName());
        }
        return userRepository.save(user);
    }

    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    public User getByName(String name) throws EntityNotFoundException {
        return userRepository.findByName(name)
            .orElseThrow(EntityNotFoundException::new);
    }

    public void assignAuthority(String name, Authority authority) {
        User user = this.getByName(name);
        user.getAuthorities().add(authority);

        userRepository.save(user);

        AuthorizationHelper.updateCurrentAuthority(user.getAuthorities());
    }

    public void revokeAuthority(String name, Authority authority) {
        User user = this.getByName(name);
        user.getAuthorities().remove(authority);

        userRepository.save(user);

        AuthorizationHelper.updateCurrentAuthority(user.getAuthorities());
    }
}
