package ru.ivamly.chill.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import ru.ivamly.chill.dto.GetChillsRs;
import ru.ivamly.chill.entity.enums.Authority;
import ru.ivamly.chill.mapper.ChillMapper;
import ru.ivamly.chill.service.ChillService;
import ru.ivamly.chill.service.UserService;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController { // TODO добавить сваггер

    private final ChillService chillService;
    private final ChillMapper chillMapper;
    private final UserService userService;

    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping("/{id}/chills")
    public GetChillsRs get(@PathVariable UUID id) {
        return new GetChillsRs(
                chillMapper.map(
                        chillService.findByUserId(id)
                )
        );
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/assignAuthority")
    public ResponseEntity<Void> assignAuthority(
        @AuthenticationPrincipal UserDetails user, @RequestBody Authority authority
    ) {
        userService.assignAuthority(user.getUsername(), authority);
        
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/revokeAuthority")
    public ResponseEntity<Void> revokeAuthority(
        @AuthenticationPrincipal UserDetails user, @RequestBody Authority authority
    ) {
        userService.revokeAuthority(user.getUsername(), authority);
        
        return ResponseEntity.ok().build();
    }
}
