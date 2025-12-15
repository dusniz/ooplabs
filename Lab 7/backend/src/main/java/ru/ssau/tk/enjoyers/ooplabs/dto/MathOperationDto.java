package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MathOperationDto {
    private Long functionId1;
    private Long functionId2;
    private Long userId;
    private String resultName;
    private String description;
}
