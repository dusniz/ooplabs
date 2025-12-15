package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FunctionFromPointsRequest {

    private Long userId;
    private String name;
    private String description;
    private String type;
    private List<PointRequest> points;
}