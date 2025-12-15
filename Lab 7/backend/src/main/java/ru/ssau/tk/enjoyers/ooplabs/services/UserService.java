package ru.ssau.tk.enjoyers.ooplabs.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;
import ru.ssau.tk.enjoyers.ooplabs.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<User> getUser(Long id) {
        logger.debug("Getting user with id: {}", id);
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        logger.debug("Finding user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public User createUser(String username, String passwordHash, Role role) {
        logger.info("Creating user: {}", username);

        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User already exists: " + username);
        }

        User user = User.builder()
                        .username(username)
                        .passwordHash(passwordHash)
                        .role(role)
                        .build();
        User savedUser = userRepository.save(user);

        logger.info("Created user with id: {}", savedUser.getId());
        return savedUser;
    }

    public User updateUser(Long userId, String username, String passwordHash, Role role) {
        logger.info("Updating user with id: {}", userId);
        User updatedUser;

        if (userRepository.existsById(userId)) {
            User user = User.builder()
                    .id(userId)
                    .username(username)
                    .passwordHash(passwordHash)
                    .role(role)
                    .build();
            updatedUser = userRepository.save(user);
        }
        else {
            throw new IllegalArgumentException("No user with such id: " + userId);
        }

        logger.info("Updated user with id: {}", updatedUser.getId());
        return updatedUser;
    }

    public boolean validateUser(String username, String passwordHash) {
        logger.debug("Validating user: {}", username);

        Optional<User> user = userRepository.findByUsername(username);
        boolean isValid = user.isPresent() && user.get().getPasswordHash().equals(passwordHash);

        logger.debug("User validation result for {}: {}", username, isValid);
        return isValid;
    }

    @Transactional(readOnly = true)
    public List<User> getUsersByRole(Role role) {
        logger.debug("Getting users by role: {}", role);
        return userRepository.findByRole(role);
    }

    public void deleteUser(Long userId) {
        logger.info("Deleting user: {}", userId);
        userRepository.deleteById(userId);
        logger.info("Deleted user: {}", userId);
    }
}