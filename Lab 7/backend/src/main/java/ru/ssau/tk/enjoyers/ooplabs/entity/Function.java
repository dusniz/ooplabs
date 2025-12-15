package ru.ssau.tk.enjoyers.ooplabs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Function {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String type;
    private Integer pointCount;
    private String functionClass;

    public Function(Long userId, String name, String type, String description, Integer pointCount, String functionClass) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.pointCount = pointCount;
        this.functionClass = functionClass;
    }
}
