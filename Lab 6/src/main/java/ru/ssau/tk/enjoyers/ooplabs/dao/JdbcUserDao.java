package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;
import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.ssau.tk.enjoyers.ooplabs.Role;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

    public class JdbcUserDao implements UserDao {
    private static final Logger logger = LogManager.getLogger(JdbcUserDao.class);

    @Override
    public Optional<UserDto> findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserDto user = mapResultSetToUser(rs);
                logger.debug("Found user by id: {}", id);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: {} - {}", id, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UserDto user = mapResultSetToUser(rs);
                logger.debug("Found user by username: {}", username);
                return Optional.of(user);
            }
        } catch (SQLException e) {
            logger.error("Error finding user by username: {} - {}", username, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<UserDto> findAll() {
        List<UserDto> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                UserDto user = mapResultSetToUser(rs);
                users.add(user);
            }

            logger.debug("Found {} users", users.size());
        } catch (SQLException e) {
            logger.error("Error finding all users: {}", e.getMessage());
        }

        return users;
    }

    @Override
    public Long save(UserDto user) {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?::user_role) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().getCode());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long userId = rs.getLong(1);
                logger.info("Saved user with id: {}, username: {}, role: {}",
                        userId, user.getUsername(), user.getRole());
                return userId;
            }
        } catch (SQLException e) {
            logger.error("Error saving user: {} - {}", user.getUsername(), e.getMessage());
        }

        return null;
    }

    private UserDto mapResultSetToUser(ResultSet rs) throws SQLException {
        return new UserDto(Long.parseLong(rs.getString("id")) ,rs.getString("username"),
                rs.getString("password_hash"), Role.fromCode(rs.getString("role")));
    }

    @Override
    public boolean update(UserDto user) {
        String sql = "UPDATE users SET username = ?, password_hash = ?, role = ?::user_role WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPasswordHash());
            stmt.setString(3, user.getRole().getCode());
            stmt.setLong(4, user.getId());

            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Updated user with id: {}, new role: {}", user.getId(), user.getRole());
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error updating user with id: {} - {}", user.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Deleted user with id: {}", id);
            } else {
                logger.warn("No user found to delete with id: {}", id);
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error deleting user with id: {} - {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            logger.debug("User existence check for {}: {}", username, exists);
            return exists;
        } catch (SQLException e) {
            logger.error("Error checking user existence: {} - {}", username, e.getMessage());
            return false;
        }
    }

    // Добавляем методы для работы с ролями
    public boolean isAdmin(Long userId) {
        return userHasRole(userId, Role.ADMIN);
    }

    public boolean isUser(Long userId) {
        return userHasRole(userId, Role.USER);
    }

    private boolean userHasRole(Long userId, Role requiredRole) {
        String sql = "SELECT 1 FROM users WHERE id = ? AND role = ?::user_role";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, requiredRole.getCode());

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            logger.error("Error checking user role: {} - {}", userId, e.getMessage());
            return false;
        }
    }
}
