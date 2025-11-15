package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDaoTest {
    private JdbcUserDao userDao;
    private static final String TEST_USERNAME = "testuser_junit";

    @BeforeEach
    void setUp() {
        StringBuilder script = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\User\\IdeaProjects\\ooplabs\\Lab 5\\src\\main\\resources\\scripts\\create_tables.sql"))) {
            String line;

            while ((line = reader.readLine()) != null) {
                script.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(script.toString());{
                stmt.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        userDao = new JdbcUserDao();
        cleanupTestUser();
    }

    @AfterEach
    void tearDown() {
        cleanupTestUser();
    }

    private void cleanupTestUser() {
        userDao.findByUsername(TEST_USERNAME).ifPresent(user -> userDao.delete(user.getId()));
    }

    @Test
    @Order(1)
    @DisplayName("Should save and find user by ID")
    void testSaveAndFindUser() {
        // Given
        UserDto user = new UserDto(TEST_USERNAME, "hashedpassword123", Role.USER);

        // When
        Long userId = userDao.save(user);

        // Then
        assertNotNull(userId, "User ID should not be null after save");

        Optional<UserDto> foundUser = userDao.findById(userId);
        assertTrue(foundUser.isPresent(), "User should be found by id");
        assertEquals(TEST_USERNAME, foundUser.get().getUsername());
        assertEquals(Role.USER, foundUser.get().getRole());
    }

    @Test
    @Order(2)
    @DisplayName("Should find user by username")
    void testFindByUsername() {
        // Given
        UserDto user = new UserDto(TEST_USERNAME, "password123");
        userDao.save(user);

        // When
        Optional<UserDto> foundUser = userDao.findByUsername(TEST_USERNAME);

        // Then
        assertAll(
                () -> assertTrue(foundUser.isPresent(), "User should be found by username"),
                () -> assertEquals(TEST_USERNAME, foundUser.get().getUsername()),
                () -> assertEquals(Role.USER, foundUser.get().getRole(), "Default role should be USER")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should check if user exists by username")
    void testExistsByUsername() {
        // Given
        UserDto user = new UserDto(TEST_USERNAME, "password");
        userDao.save(user);

        // When & Then
        assertAll(
                () -> assertTrue(userDao.existsByUsername(TEST_USERNAME)),
                () -> assertFalse(userDao.existsByUsername("nonexistentuser"))
        );
    }

    @Test
    @Order(5)
    @DisplayName("Should update user information")
    void testUpdateUser() {
        UserDto user = new UserDto(TEST_USERNAME, "oldpassword");
        Long userId = userDao.save(user);

        UserDto userToUpdate = new UserDto(userId, TEST_USERNAME, "newpassword", Role.ADMIN);
        boolean updateResult = userDao.update(userToUpdate);

        assertTrue(updateResult, "Update should be successful");

        Optional<UserDto> updatedUser = userDao.findById(userId);
        assertAll(
                () -> assertTrue(updatedUser.isPresent()),
                () -> assertEquals(Role.ADMIN, updatedUser.get().getRole())
        );
    }

    @Test
    @Order(6)
    @DisplayName("Should delete user")
    void testDeleteUser() {
        // Given
        UserDto user = new UserDto(TEST_USERNAME, "password");
        Long userId = userDao.save(user);

        // When
        boolean deleteResult = userDao.delete(userId);

        // Then
        assertAll(
                () -> assertTrue(deleteResult, "Delete should be successful"),
                () -> assertFalse(userDao.findById(userId).isPresent(),
                        "User should be deleted")
        );
    }

    @Test
    @Order(7)
    @DisplayName("Should find all users")
    void testFindAllUsers() {
        // Given
        UserDto user1 = new UserDto("user1_junit", "pass1");
        UserDto user2 = new UserDto("user2_junit", "pass2");
        userDao.save(user1);
        userDao.save(user2);

        // When
        List<UserDto> users = userDao.findAll();

        // Then
        assertTrue(users.size() >= 2, "Should find at least 2 users");

        // Cleanup
        userDao.findByUsername("user1_junit").ifPresent(u -> userDao.delete(u.getId()));
        userDao.findByUsername("user2_junit").ifPresent(u -> userDao.delete(u.getId()));
    }

    @Test
    @Order(8)
    @DisplayName("Should handle non-existent user")
    void testNonExistentUser() {
        // When
        Optional<UserDto> user = userDao.findById(999999L);

        // Then
        assertFalse(user.isPresent(), "Non-existent user should not be found");
    }
}
