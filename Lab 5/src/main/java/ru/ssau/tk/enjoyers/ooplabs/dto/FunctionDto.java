package ru.ssau.tk.enjoyers.ooplabs.dto;

public class FunctionDto {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String type;
    private Integer pointCount;
    private String functionClass;

    public FunctionDto(Long id, Long userId, String name, String type, String description, Integer pointCount, String functionClass) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.pointCount = pointCount;
        this.functionClass = functionClass;
    }

    public FunctionDto(Long userId, String name, String type, String description, Integer pointCount, String functionClass) {
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        this.pointCount = pointCount;
        this.functionClass = functionClass;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
