package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointRequest {

    private Long functionId;
    private Double x;
    private Double y;
    private Integer index;
}

