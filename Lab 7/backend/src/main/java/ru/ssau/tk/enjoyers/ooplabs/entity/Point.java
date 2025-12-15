package ru.ssau.tk.enjoyers.ooplabs.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Point {
    private Long id;
    private Long functionId;
    private Double x;
    private Double y;
    private Integer index;

    public Point(Long functionId, Double x, Double y, Integer index) {
        this.functionId = functionId;
        this.x = x;
        this.y = y;
        this.index = index;
    }
}
