package ru.ssau.tk.enjoyers.ooplabs.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "functions")
@AllArgsConstructor
@NoArgsConstructor
public class Function {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Getter
    @Setter
    @Column(nullable = false, length = 255)
    private String name;

    @Getter
    @Setter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @Setter
    @Column(nullable = false, length = 50)
    private String type; // TABULATED, MATH

    @Getter
    @Setter
    @Column(name = "point_count")
    private Integer pointCount = 0;

    @Getter
    @Setter
    @Column(name = "function_class", length = 255)
    private String functionClass;
}