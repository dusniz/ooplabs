package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionListResponse {
    private List<String> availableFunctions;
    private Integer totalCount;
}
