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
        String sql = "SELECT * FROM points WHERE function_id = ? ORDER BY index";

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
        String sql = "INSERT INTO points (function_id, x, y, index) VALUES (?, ?, ?, ?) RETURNING id";

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
        return new PointDto(rs.getLong("id"), rs.getLong("function_id"),
                rs.getDouble("x"), rs.getDouble("y"), rs.getInt("index"));
    }

    @Override
    public Optional<PointDto> findById(Long id) {
        String sql = "SELECT * FROM points WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                PointDto point = mapResultSetToPoint(rs);
                logger.debug("Found point by id: {}", id);
                return Optional.of(point);
            }
        } catch (SQLException e) {
            logger.error("Error finding point by id: {} - {}", id, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public Optional<PointDto> findByFunctionIdAndIndex(Long functionId, Integer index) {
        String sql = "SELECT * FROM points WHERE function_id = ? AND index = ?";

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
            logger.error("Error finding point by function id and index: {}, {} - {}",
                    functionId, index, e.getMessage());
        }

        return Optional.empty();
    }

    @Override
    public boolean update(PointDto point) {
        String sql = "UPDATE points SET x = ?, y = ?, index = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, point.getX());
            stmt.setDouble(2, point.getY());
            stmt.setInt(3, point.getIndex());
            stmt.setLong(4, point.getId());

            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.debug("Updated point with id: {}", point.getId());
            } else {
                logger.warn("No point found to update with id: {}", point.getId());
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error updating point with id: {} - {}", point.getId(), e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM points WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
            int affectedRows = stmt.executeUpdate();
            boolean success = affectedRows > 0;

            if (success) {
                logger.debug("Deleted point with id: {}", id);
            } else {
                logger.warn("No point found to delete with id: {}", id);
            }

            return success;
        } catch (SQLException e) {
            logger.error("Error deleting point with id: {} - {}", id, e.getMessage());
            return false;
        }
    }

    @Override
    public int countByFunctionId(Long functionId) {
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
}
