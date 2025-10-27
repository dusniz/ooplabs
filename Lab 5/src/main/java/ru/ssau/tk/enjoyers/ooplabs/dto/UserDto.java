package ru.ssau.tk.enjoyers.ooplabs.dto;

import ru.ssau.tk.enjoyers.ooplabs.Role;

public class UserDto {
    private Long id;
    private String username;
    private String passwordHash;
    private Role role;

    public UserDto(Long id, String username, String passwordHash) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

}
