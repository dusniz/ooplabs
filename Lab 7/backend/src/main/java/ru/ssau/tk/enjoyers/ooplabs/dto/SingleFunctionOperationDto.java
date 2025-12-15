package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SingleFunctionOperationDto {
    private Long functionId;
    private Long userId;
    private String resultName;
    private String description;
}
