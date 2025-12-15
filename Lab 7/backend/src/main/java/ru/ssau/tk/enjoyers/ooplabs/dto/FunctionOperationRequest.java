package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionOperationRequest {
    private Long firstFunctionId;
    private Long secondFunctionId;
    private String operation; // ADD, SUBTRACT, MULTIPLY, DIVIDE, COMPOSITE
}
