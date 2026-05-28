package ru.ivamly.chill.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ivamly.chill.dto.CreateChillRq;
import ru.ivamly.chill.dto.CreateChillRs;
import ru.ivamly.chill.mapper.ChillMapper;
import ru.ivamly.chill.service.ChillService;

@RestController
@RequestMapping("chill")
@RequiredArgsConstructor
public class ChillController { // TODO добавить сваггер

    private final ChillService chillService;
    private final ChillMapper chillMapper;

    @PostMapping
    public CreateChillRs create(@RequestBody @Valid CreateChillRq request) {
        return chillMapper.map(
                chillService.create(
                        chillMapper.map(request)
                )
        );
    }
}
