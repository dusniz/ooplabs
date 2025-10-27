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

    @Column(name = "index", nullable = false)
    private Integer index;

    public Point(Long functionId, Double x, Double y, Integer index) {
        this.functionId = functionId;
        this.x = x;
        this.y = y;
        this.index = index;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }

    public Long getFunctionId() { return functionId; }
    public void setFunctionId(Long functionId) { this.functionId = functionId; }

    public Double getX() { return x; }
    public void setX(Double x) { this.x = x; }

    public Double getY() { return y; }
    public void setY(Double y) { this.y = y; }

    public Integer getIndex() { return index; }
    public void setIndex(Integer index) { this.index = index; }
}