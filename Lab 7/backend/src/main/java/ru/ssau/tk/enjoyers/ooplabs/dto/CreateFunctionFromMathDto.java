package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateFunctionFromMathDto {
    private Long userId;
    private String name;
    private String description;
    private String mathExpression;
    private Double startX;
    private Double endX;
    private Integer pointCount;
}
