package ru.ssau.tk.enjoyers.ooplabs.mappers;

import org.mapstruct.*;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;

@Mapper(componentModel = "spring")
public interface FunctionMapper {

    FunctionDto FunctiontoFunctionDto(Function function);

    @Mapping(target = "id", ignore = true)
    Function FunctionDtotoFunction(FunctionDto functionDto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(FunctionDto functionDto, @org.mapstruct.MappingTarget Function function);
}
