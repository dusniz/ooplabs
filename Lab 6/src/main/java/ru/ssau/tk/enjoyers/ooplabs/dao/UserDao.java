package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;
import java.util.List;
import java.util.Optional;

public interface UserDao {
    Optional<UserDto> findById(Long id);
    Optional<UserDto> findByUsername(String username);
    List<UserDto> findAll();
    Long save(UserDto user);
    boolean update(UserDto user);
    boolean delete(Long id);
    boolean existsByUsername(String username);
}
