package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TabulatedFunctionRequest{

    private Long userId;
    private String name;
    private String description;
    private String type;
    private Integer pointCount;
    private String functionClass;
}
