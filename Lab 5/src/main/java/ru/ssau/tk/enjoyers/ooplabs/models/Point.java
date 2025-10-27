package ru.ssau.tk.enjoyers.ooplabs.models;

import jakarta.persistence.*;

@Entity
@Table(name = "points")
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "function_id", nullable = false)
    private Long functionId;

    @Column(nullable = false)
    private Double x;

    @Column(nullable = false)
    private Double y;

    @Column(name = "point_index", nullable = false)
    private Integer pointIndex;

    public Point(Long functionId, Double x, Double y, Integer pointIndex) {
        this.functionId = functionId;
        this.x = x;
        this.y = y;
        this.pointIndex = pointIndex;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }

    public Long getFunction() { return functionId; }
    public void setFunction(Long functionId) { this.functionId = functionId; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Integer getPointIndex() { return pointIndex; }
    public void setPointIndex(Integer pointIndex) { this.pointIndex = pointIndex; }
}