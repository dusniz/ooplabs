package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluationResponse {
    private Long functionId;
    private Double x;
    private Double y;
}
