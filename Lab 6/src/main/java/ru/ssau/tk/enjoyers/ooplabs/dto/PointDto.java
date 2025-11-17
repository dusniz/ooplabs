package ru.ssau.tk.enjoyers.ooplabs.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class PointDto {
    private Long id;
    private Long functionId;
    private Double x;
    private Double y;
    private Integer index;

    public PointDto(Long functionId, Double x, Double y, Integer index) {
        this.functionId = functionId;
        this.x = x;
        this.y = y;
        this.index = index;
    }

    public PointDto(Long id, Long functionId, Double x, Double y, Integer index) {
        this.id = id;
        this.functionId = functionId;
        this.x = x;
        this.y = y;
        this.index = index;
    }
}
