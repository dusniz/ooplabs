package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FactoryResponse {
    private String factoryType;
    private String description;
}
