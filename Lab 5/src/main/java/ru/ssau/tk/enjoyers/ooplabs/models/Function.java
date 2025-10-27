package ru.ssau.tk.enjoyers.ooplabs.models;

import jakarta.persistence.*;

@Entity
@Table(name = "functions")
public class Function {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 50)
    private String type; // TABULATED_ARRAY, TABULATED_LINKED_LIST, MATH, COMPOSITE

    @Column(name = "points_count")
    private Integer pointsCount = 0;

    @Column(name = "function_class", length = 255)
    private String functionClass;

    public Function(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Integer getPointsCount() { return pointsCount; }
    public void setPointsCount(Integer pointsCount) { this.pointsCount = pointsCount; }

    public String getFunctionClass() { return functionClass; }
    public void setFunctionClass(String functionClass) { this.functionClass = functionClass; }
}