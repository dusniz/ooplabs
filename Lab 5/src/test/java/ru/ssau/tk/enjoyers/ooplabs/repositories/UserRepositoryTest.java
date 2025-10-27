package ru.ssau.tk.enjoyers.ooplabs.repositories;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.models.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("testuser_spring", "hashedpassword", Role.USER);
        entityManager.persistAndFlush(testUser);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find user by username")
    void findByUsername() {
        // When
        Optional<User> found = userRepository.findByUsername("testuser_spring");

        // Then
        assertAll(
                () -> assertTrue(found.isPresent()),
                () -> assertEquals("testuser_spring", found.get().getUsername()),
                () -> assertEquals(Role.USER, found.get().getRole())
        );
    }

    @Test
    @DisplayName("Should check if user exists by username")
    void existsByUsername() {
        // When & Then
        assertAll(
                () -> assertTrue(userRepository.existsByUsername("testuser_spring")),
                () -> assertFalse(userRepository.existsByUsername("nonexistent"))
        );
    }

    @Test
    @DisplayName("Should find users by role")
    void findByRole() {
        // Given
        User adminUser = new User("admin_spring", "password", Role.ADMIN);
        entityManager.persistAndFlush(adminUser);

        // When
        List<User> users = userRepository.findByRole(Role.ADMIN);

        // Then
        assertAll(
                () -> assertFalse(users.isEmpty()),
                () -> assertTrue(users.stream().allMatch(user -> user.getRole() == Role.ADMIN))
        );
    }

    @Test
    @DisplayName("Should not find non-existent user")
    void findByUsername_NonExistent() {
        // When
        Optional<User> found = userRepository.findByUsername("nonexistent_user");

        // Then
        assertFalse(found.isPresent());
    }
}