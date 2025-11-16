package ru.ssau.tk.enjoyers.ooplabs.entities;

import ru.ssau.tk.enjoyers.ooplabs.Role;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @Getter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Getter
    @Setter
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;
}
