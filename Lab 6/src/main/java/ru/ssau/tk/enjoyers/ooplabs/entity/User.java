package ru.ssau.tk.enjoyers.ooplabs.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.ssau.tk.enjoyers.ooplabs.Role;

@Getter
@Setter
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private String passwordHash;
    private Role role;

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = Role.USER;
    }

    public User(String username, String passwordHash, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public User(Long id, String username, String passwordHash, Role role) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }
}
