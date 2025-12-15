package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CompositeFunctionDto {
    private Long outerFunctionId;
    private Long innerFunctionId;
    private Long userId;
    private String resultName;
    private String description;
}
