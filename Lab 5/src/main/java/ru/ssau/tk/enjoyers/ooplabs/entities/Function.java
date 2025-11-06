package ru.ssau.tk.enjoyers.ooplabs.entities;

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

    @Column(name = "point_count")
    private Integer pointCount = 0;

    @Column(name = "function_class", length = 255)
    private String functionClass;

    public Function(Long userId, String name, String description, String type, Integer pointCount,
                    String functionClass) {
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.type = type;
        this.pointCount = pointCount;
        this.functionClass = functionClass;
    }

    public Function() {}

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

    public Integer getPointCount() { return pointCount; }
    public void setPointCount(Integer pointCount) { this.pointCount = pointCount; }

    public String getFunctionClass() { return functionClass; }
    public void setFunctionClass(String functionClass) { this.functionClass = functionClass; }
}