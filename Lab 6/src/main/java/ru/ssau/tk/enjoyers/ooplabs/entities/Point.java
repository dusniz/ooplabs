package ru.ssau.tk.enjoyers.ooplabs.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "points")
@NoArgsConstructor
@AllArgsConstructor
public class Point {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "function_id", nullable = false)
    private Long functionId;

    @Getter
    @Setter
    @Column(nullable = false)
    private Double x;

    @Getter
    @Setter
    @Column(nullable = false)
    private Double y;

    @Getter
    @Setter
    @Column(name = "index", nullable = false)
    private Integer index;
}