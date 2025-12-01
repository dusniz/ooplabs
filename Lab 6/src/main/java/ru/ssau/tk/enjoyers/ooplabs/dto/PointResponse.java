package ru.ssau.tk.enjoyers.ooplabs.dto;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointResponse {

    @Id
    private Long id;
    private Long functionId;
    private Double x;
    private Double y;
    private Integer index;
}
