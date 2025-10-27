package ru.ssau.tk.enjoyers.ooplabs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.models.User;
import ru.ssau.tk.enjoyers.ooplabs.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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

        User user = new User(username, passwordHash, role);
        User savedUser = userRepository.save(user);

        logger.info("Created user with id: {}", savedUser.getId());
        return savedUser;
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