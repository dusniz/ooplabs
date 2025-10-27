package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class JdbcFunctionDao implements FunctionDao {
    private static final Logger logger = LogManager.getLogger(JdbcFunctionDao.class);

    @Override
    public Optional<FunctionDto> findById(Long id) {
        String sql = "SELECT * FROM functions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                FunctionDto function = mapResultSetToFunction(rs);
                logger.debug("Found function: {}", function);
                return Optional.of(function);
            }
        } catch (SQLException e) {
            logger.error("Error finding function by id: {} - {}", id, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public List<FunctionDto> findByUserId(Long userId) {
        List<FunctionDto> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE user_id = ? ORDER BY created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FunctionDto function = mapResultSetToFunction(rs);
                functions.add(function);
            }

            logger.info("Found " + functions.size() + " functions for user: " + userId);
        } catch (SQLException e) {
            logger.error("Error finding functions by user id: " + userId + " - " + e.getMessage());
        }

        return functions;
    }

    @Override
    public List<FunctionDto> findByUserIdAndType(Long userId, String type) {
        List<FunctionDto> functions = new ArrayList<>();
        String sql = "SELECT * FROM functions WHERE user_id = ? AND type = ? ORDER BY name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, userId);
            stmt.setString(2, type);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                FunctionDto function = mapResultSetToFunction(rs);
                functions.add(function);
            }

            logger.debug("Found {} functions of type {} for user id: {}", functions.size(), type, userId);
        } catch (SQLException e) {
            logger.error("Error finding functions by user id and type: {}, {} - {}", userId, type, e.getMessage());
        }

        return functions;
    }

    @Override
    public Long save(FunctionDto function) {
        String sql = "INSERT INTO functions (user_id, name, description, type, left_bound, right_bound, points_count, function_class) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            setFunctionParameters(stmt, function);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long functionId = rs.getLong(1);
                logger.info("Saved function: {}", function);
                return functionId;
            }
        } catch (SQLException e) {
            logger.error("Error saving function: {} - {}", function, e.getMessage());
        }

        return null;
    }

    @Override
    public boolean update(FunctionDto function) {
        String sql = "UPDATE functions SET name = ?, description = ?, type = ?, left_bound = ?, right_bound = ?, " +
                "points_count = ?, function_class = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, function.getName());
            stmt.setString(2, function.getDescription());
            stmt.setString(3, function.getType());

            stmt.setInt(6, function.getPointCount());
            stmt.setString(7, function.getFunctionClass());
            stmt.setLong(8, function.getId());

            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Updated function with id: {}", function.getId());
            } else {
                logger.warn("No function found to update with id: {}", function.getId());
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error updating function with id: {} - {}", function.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        // Сначала удаляем точки (каскадно в БД, но лучше явно для логирования)
        deleteAllPointsByFunctionId(id);

        String sql = "DELETE FROM functions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.info("Deleted function with id: {}", id);
            } else {
                logger.warn("No function found to delete with id: {}", id);
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error deleting function with id: {} - {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM functions WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next();

            logger.debug("Function existence check for id {}: {}", id, exists);
            return exists;
        } catch (SQLException e) {
            logger.error("Error checking function existence for id: {} - {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public List<PointDto> findPointsByFunctionId(Long functionId) {
        List<PointDto> points = new ArrayList<>();
        String sql = "SELECT * FROM points WHERE function_id = ? ORDER BY point_index";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PointDto point = mapResultSetToPoint(rs);
                points.add(point);
            }

            logger.debug("Found {} points for function id: {}", points.size(), functionId);
        } catch (SQLException e) {
            logger.error("Error finding points for function id: {} - {}", functionId, e.getMessage());
        }

        return points;
    }

    @Override
    public Optional<PointDto> findPointByFunctionIdAndIndex(Long functionId, Integer index) {
        String sql = "SELECT * FROM points WHERE function_id = ? AND point_index = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            stmt.setInt(2, index);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PointDto point = mapResultSetToPoint(rs);
                logger.debug("Found point at index {} for function id: {}", index, functionId);
                return Optional.of(point);
            }
        } catch (SQLException e) {
            logger.error("Error finding point at index {} for function id: {} - {}", index, functionId, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public void savePoints(Long functionId, List<PointDto> points) {
        String sql = "INSERT INTO points (function_id, x, y, point_index) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (PointDto point : points) {
                stmt.setLong(1, functionId);
                stmt.setDouble(2, point.getX());
                stmt.setDouble(3, point.getY());
                stmt.setInt(4, point.getIndex());
                stmt.addBatch();
            }

            int[] results = stmt.executeBatch();
            logger.info("Saved {} points for function id: {}", results.length, functionId);

            // Обновляем points_count в функции
            updatePointsCount(functionId, points.size());

        } catch (SQLException e) {
            logger.error("Error saving points for function id: {} - {}", functionId, e.getMessage());
        }
    }

    @Override
    public boolean updatePoint(Long functionId, PointDto point) {
        String sql = "UPDATE points SET x = ?, y = ? WHERE function_id = ? AND point_index = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, point.getX());
            stmt.setDouble(2, point.getY());
            stmt.setLong(3, functionId);
            stmt.setInt(4, point.getIndex());

            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.debug("Updated point at index {} for function id: {}", point.getIndex(), functionId);
            } else {
                logger.warn("No point found to update at index {} for function id: {}", point.getIndex(), functionId);
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error updating point at index {} for function id: {} - {}",
                    point.getIndex(), functionId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deletePoint(Long pointId) {
        String sql = "DELETE FROM points WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pointId);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.debug("Deleted point with id: {}", pointId);

                // Нужно обновить points_count для функции
                // Для этого нужно найти function_id точки
                Optional<Long> functionId = findFunctionIdByPointId(pointId);
                functionId.ifPresent(this::updatePointsCountBasedOnActualPoints);

            } else {
                logger.warn("No point found to delete with id: {}", pointId);
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error deleting point with id: {} - {}", pointId, e.getMessage());
            return false;
        }
    }

    @Override
    public boolean deleteAllPointsByFunctionId(Long functionId) {
        String sql = "DELETE FROM points WHERE function_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            int affectedRows = stmt.executeUpdate();

            // Обновляем points_count
            updatePointsCount(functionId, 0);

            logger.debug("Deleted {} points for function id: {}", affectedRows, functionId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting points for function id: {} - {}", functionId, e.getMessage());
            return false;
        }
    }

    @Override
    public int countPointsByFunctionId(Long functionId) {
        String sql = "SELECT COUNT(*) FROM points WHERE function_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                logger.debug("Counted {} points for function id: {}", count, functionId);
                return count;
            }
        } catch (SQLException e) {
            logger.error("Error counting points for function id: {} - {}", functionId, e.getMessage());
        }

        return 0;
    }

    // Вспомогательные методы
    private FunctionDto mapResultSetToFunction(ResultSet rs) throws SQLException {
        FunctionDto function = new FunctionDto();
        function.setUserId(rs.getLong("user_id"));
        function.setName(rs.getString("name"));
        function.setDescription(rs.getString("description"));
        function.setType(rs.getString("type"));
        function.setPointCount(rs.getInt("points_count"));
        function.setFunctionClass(rs.getString("function_class"));
        return function;
    }

    private PointDto mapResultSetToPoint(ResultSet rs) throws SQLException {
        return new PointDto(rs.getLong("function_id"), rs.getDouble("x"),
                rs.getDouble("y"), rs.getInt("point_index"));
    }

    private void setFunctionParameters(PreparedStatement stmt, FunctionDto function) throws SQLException {
        stmt.setLong(1, function.getUserId());
        stmt.setString(2, function.getName());
        stmt.setString(3, function.getDescription());
        stmt.setString(4, function.getType());

        if (function.getPointCount() != null) {
            stmt.setInt(7, function.getPointCount());
        } else {
            stmt.setNull(7, Types.INTEGER);
        }

        stmt.setString(8, function.getFunctionClass());
    }

    private void updatePointsCount(Long functionId, int count) {
        String sql = "UPDATE functions SET points_count = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, count);
            stmt.setLong(2, functionId);
            stmt.executeUpdate();

            logger.debug("Updated points_count to {} for function id: {}", count, functionId);
        } catch (SQLException e) {
            logger.error("Error updating points_count for function id: {} - {}", functionId, e.getMessage());
        }
    }

    private void updatePointsCountBasedOnActualPoints(Long functionId) {
        int actualCount = countPointsByFunctionId(functionId);
        updatePointsCount(functionId, actualCount);
    }

    private Optional<Long> findFunctionIdByPointId(Long pointId) {
        String sql = "SELECT function_id FROM points WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, pointId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return Optional.of(rs.getLong("function_id"));
            }
        } catch (SQLException e) {
            logger.error("Error finding function_id for point id: {} - {}", pointId, e.getMessage());
        }

        return Optional.empty();
    }
}