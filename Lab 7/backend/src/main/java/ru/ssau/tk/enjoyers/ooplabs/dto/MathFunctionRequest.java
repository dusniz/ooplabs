package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MathFunctionRequest {
    private Long userId;
    private String name;
    private String description;
    private String functionClass;
}
