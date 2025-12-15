package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationRequest {
    private Long functionId;
    private String variable;
    private Double lowerLimit;
    private Double upperLimit;
}
