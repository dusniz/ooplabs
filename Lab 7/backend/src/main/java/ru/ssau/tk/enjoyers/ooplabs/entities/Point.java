package ru.ssau.tk.enjoyers.ooplabs.entities;

import jakarta.persistence.*;
import lombok.*;

@Data
@Builder
@Entity
@Table(name = "points")
@NoArgsConstructor
@AllArgsConstructor
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
}