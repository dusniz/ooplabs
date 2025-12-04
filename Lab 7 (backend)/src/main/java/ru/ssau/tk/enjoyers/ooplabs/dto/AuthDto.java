package ru.ssau.tk.enjoyers.ooplabs.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class AuthDto {

    private String username;
    private String password;
}
