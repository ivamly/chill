package ru.ivamly.chill.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivamly.chill.dto.GetChillsRs;
import ru.ivamly.chill.mapper.ChillMapper;
import ru.ivamly.chill.service.ChillService;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController { // TODO добавить сваггер

    private final ChillService chillService;
    private final ChillMapper chillMapper;

    @GetMapping("/{id}/chills")
    public GetChillsRs get(@PathVariable UUID id) {
        return new GetChillsRs(
                chillMapper.map(
                        chillService.findByUserId(id)
                )
        );
    }
}
