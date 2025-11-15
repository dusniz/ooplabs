package ru.ssau.tk.enjoyers.ooplabs.dto;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
}
