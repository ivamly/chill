package ru.ivamly.chill.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.ivamly.chill.config.MapstructConfig;
import ru.ivamly.chill.dto.ChillInfo;
import ru.ivamly.chill.dto.CreateChillRq;
import ru.ivamly.chill.dto.CreateChillRs;
import ru.ivamly.chill.dto.GetChillRs;
import ru.ivamly.chill.entity.Chill;

import java.util.Collection;

@Mapper(config = MapstructConfig.class)
public interface ChillMapper {

    @Mapping(target = "id", ignore = true)
    Chill map(CreateChillRq source);

    ChillInfo map(Chill source);

    Collection<ChillInfo> map(Collection<Chill> source);

    CreateChillRs mapToCreateChillRs(Chill source);

    GetChillRs mapToGetChillRs(Chill source);
}
