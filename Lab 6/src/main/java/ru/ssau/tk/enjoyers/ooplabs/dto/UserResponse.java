package ru.ssau.tk.enjoyers.ooplabs.dto;

import jakarta.persistence.Id;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    @Id
    private Long id;
    private String username;
    private String passwordHash;
    private Role role;
}
