package ru.ssau.tk.enjoyers.ooplabs.dto;

import ru.ssau.tk.enjoyers.ooplabs.Role;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private String username;
    private String passwordHash;
    private Role role;
}
