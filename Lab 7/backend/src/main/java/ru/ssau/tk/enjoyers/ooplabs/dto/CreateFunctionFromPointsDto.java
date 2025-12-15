package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateFunctionFromPointsDto {
    private Long userId;
    private String name;
    private String description;
    private String type;
    private String functionClass;
    private List<Point> points;
}
