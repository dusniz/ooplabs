package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class FunctionDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String type;
    private Integer pointCount;
    private String functionClass;

    public FunctionDto(Long userId, String name, String type, String description, Integer pointCount, String functionClass) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.pointCount = pointCount;
        this.functionClass = functionClass;
    }
}
