package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointDto {
    private Long id;
    private Long functionId;
    private Double x;
    private Double y;
    private Integer index;
}
