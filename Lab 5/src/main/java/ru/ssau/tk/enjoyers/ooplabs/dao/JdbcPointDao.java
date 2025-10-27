package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcPointDao implements PointDao {
    private static final Logger logger = LogManager.getLogger(JdbcPointDao.class);

    @Override
    public List<PointDto> findByFunctionId(Long functionId) {
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
            logger.error("Error finding points by function id: {} - {}", functionId, e.getMessage());
        }

        return points;
    }

    @Override
    public Long save(PointDto point) {
        String sql = "INSERT INTO points (function_id, x, y, point_index) VALUES (?, ?, ?, ?) RETURNING id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, point.getFunctionId());
            stmt.setDouble(2, point.getX());
            stmt.setDouble(3, point.getY());
            stmt.setInt(4, point.getIndex());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long pointId = rs.getLong(1);
                logger.debug("Saved point with id: {} for function id: {}", pointId, point.getFunctionId());
                return pointId;
            }
        } catch (SQLException e) {
            logger.error("Error saving point for function id: {} - {}", point.getFunctionId(), e.getMessage());
        }

        return null;
    }

    @Override
    public boolean deleteByFunctionId(Long functionId) {
        String sql = "DELETE FROM points WHERE function_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, functionId);
            int affectedRows = stmt.executeUpdate();

            logger.debug("Deleted {} points for function id: {}", affectedRows, functionId);
            return affectedRows > 0;
        } catch (SQLException e) {
            logger.error("Error deleting points for function id: {} - {}", functionId, e.getMessage());
            return false;
        }
    }

    private PointDto mapResultSetToPoint(ResultSet rs) throws SQLException {
        return new PointDto(rs.getLong("function_id"), rs.getDouble("x"),
                rs.getDouble("y"), rs.getInt("point_index"));
    }

    // Остальные методы...
    @Override
    public Optional<PointDto> findById(Long id) {
        // реализация
        return Optional.empty();
    }

    @Override
    public Optional<PointDto> findByFunctionIdAndIndex(Long functionId, Integer index) {
        // реализация
        return Optional.empty();
    }

    @Override
    public boolean update(PointDto point) {
        // реализация
        return false;
    }

    @Override
    public boolean delete(Long id) {
        // реализация
        return false;
    }

    @Override
    public int countByFunctionId(Long functionId) {
        // реализация
        return 0;
    }
}
