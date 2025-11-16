package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String type;
    private Integer pointCount;
    private String functionClass;
}
