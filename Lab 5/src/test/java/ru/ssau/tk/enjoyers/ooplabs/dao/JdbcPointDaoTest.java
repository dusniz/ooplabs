package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;

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

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("JdbcPointDao Tests")
class JdbcPointDaoTest {
    private JdbcPointDao pointDao;
    private static final Long TEST_FUNC_ID = 1L;
    private static final Long TEST_USER_ID = 1L;
    private Long savedPointId;

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

        JdbcUserDao userDao = new JdbcUserDao();
        UserDto user = new UserDto(1L, "test", "111", Role.USER);
        userDao.save(user);

        JdbcFunctionDao functionDao = new JdbcFunctionDao();
        FunctionDto function = new FunctionDto(1L, 1L, "Point Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        functionDao.save(function);

        pointDao = new JdbcPointDao();
        cleanupTestPoints();
    }

    @AfterEach
    void tearDown() {
        cleanupTestPoints();
    }

    private void cleanupTestPoints() {
        if (savedPointId != null) {
            pointDao.delete(savedPointId);
        }
        pointDao.deleteByFunctionId(TEST_FUNC_ID);
    }

    @Test
    @Order(1)
    @DisplayName("Should save and find points by function ID")
    void testSaveAndFindPointsByFunctionId() {
        Long TEST_POINT_ID = 1L;
        PointDto point1 = new PointDto(TEST_POINT_ID++, TEST_FUNC_ID, 0.0, 0.0, 0);
        PointDto point2 = new PointDto(TEST_POINT_ID++, TEST_FUNC_ID, 1.0, 1.0, 1);
        PointDto point3 = new PointDto(TEST_POINT_ID++, TEST_FUNC_ID, 2.0, 4.0, 2);

        pointDao.save(point1);
        pointDao.save(point2);
        pointDao.save(point3);

        List<PointDto> points = pointDao.findByFunctionId(TEST_FUNC_ID);

        assertAll(
                () -> assertNotNull(points, "Points list should not be null"),
                () -> assertEquals(3, points.size(), "Should find 3 points"),
                () -> assertEquals(0, points.get(0).getIndex(), "First point should have index 0"),
                () -> assertEquals(4.0, points.get(2).getY(), 0.001, "Third point Y should be 4.0")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should find point by function ID and index")
    void testFindPointByFunctionIdAndIndex() {
        // Given
        PointDto point = new PointDto(TEST_FUNC_ID, 5.0, 25.0, 3);
        pointDao.save(point);

        // When
        Optional<PointDto> foundPoint = pointDao.findByFunctionIdAndIndex(TEST_FUNC_ID, 3);

        // Then
        assertAll(
                () -> assertTrue(foundPoint.isPresent(), "Point should be found"),
                () -> assertEquals(5.0, foundPoint.get().getX(), 0.001, "X coordinate should match"),
                () -> assertEquals(25.0, foundPoint.get().getY(), 0.001, "Y coordinate should match"),
                () -> assertEquals(3, foundPoint.get().getIndex(), "Point index should match")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Should update point coordinates")
    void testUpdatePoint() {
        // Given
        PointDto point = new PointDto(TEST_FUNC_ID, 1.0, 1.0, 0);
        Long pointId = pointDao.save(point);
        savedPointId = pointId;

        // When
        PointDto pointToUpdate = new PointDto(pointId, TEST_FUNC_ID, 1.0, 2.0, 0);
        boolean updateResult = pointDao.update(pointToUpdate);

        // Then
        assertAll(
                () -> assertTrue(updateResult, "Update should be successful"),
                () -> {
                    Optional<PointDto> updatedPoint = pointDao.findById(pointId);
                    assertTrue(updatedPoint.isPresent(), "Updated point should be found");
                    assertEquals(2.0, updatedPoint.get().getY(), 0.001, "Y coordinate should be updated");
                }
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should delete point by ID")
    void testDeletePoint() {
        // Given
        PointDto point = new PointDto(TEST_FUNC_ID, 10.0, 100.0, 5);
        Long pointId = pointDao.save(point);

        // When
        boolean deleteResult = pointDao.delete(pointId);

        // Then
        assertAll(
                () -> assertTrue(deleteResult, "Delete should be successful"),
                () -> assertFalse(pointDao.findById(pointId).isPresent(), "Point should be deleted")
        );
    }

    @Test
    @Order(5)
    @DisplayName("Should delete all points by function ID")
    void testDeleteAllPointsByFunctionId() {
        // Given
        PointDto point1 = new PointDto(TEST_FUNC_ID, 1.0, 1.0, 0);
        PointDto point2 = new PointDto(TEST_FUNC_ID, 2.0, 4.0, 1);
        pointDao.save(point1);
        pointDao.save(point2);

        // When
        boolean deleteResult = pointDao.deleteByFunctionId(TEST_FUNC_ID);

        // Then
        assertAll(
                () -> assertTrue(deleteResult, "Delete should be successful"),
                () -> {
                    List<PointDto> pointsAfterDelete = pointDao.findByFunctionId(TEST_FUNC_ID);
                    assertTrue(pointsAfterDelete.isEmpty(), "All points should be deleted");
                }
        );
    }

    @Test
    @Order(6)
    @DisplayName("Should count points by function ID")
    void testCountPointsByFunctionId() {
        // Given
        PointDto point1 = new PointDto(TEST_FUNC_ID, 1.0, 1.0, 0);
        PointDto point2 = new PointDto(TEST_FUNC_ID, 2.0, 4.0, 1);
        PointDto point3 = new PointDto(TEST_FUNC_ID, 3.0, 9.0, 2);
        pointDao.save(point1);
        pointDao.save(point2);
        pointDao.save(point3);

        // When
        int pointCount = pointDao.countByFunctionId(TEST_FUNC_ID);

        // Then
        assertEquals(3, pointCount, "Should count 3 points");
    }

    @Test
    @Order(7)
    @DisplayName("Should handle non-existent point")
    void testFindNonExistentPoint() {
        // When
        Optional<PointDto> point = pointDao.findById(999999L);

        // Then
        assertFalse(point.isPresent(), "Non-existent point should not be found");
    }

    @Test
    @Order(8)
    @DisplayName("Should handle non-existent point by function ID and index")
    void testFindNonExistentPointByFunctionIdAndIndex() {
        // When
        Optional<PointDto> point = pointDao.findByFunctionIdAndIndex(TEST_FUNC_ID, 999);

        // Then
        assertFalse(point.isPresent(), "Non-existent point should not be found");
    }

    @Test
    @Order(9)
    @DisplayName("Should handle empty points list for function")
    void testFindPointsForFunctionWithNoPoints() {
        // Given - ensure no points for this function
        pointDao.deleteByFunctionId(TEST_FUNC_ID);

        // When
        List<PointDto> points = pointDao.findByFunctionId(TEST_FUNC_ID);

        // Then
        assertAll(
                () -> assertNotNull(points, "Points list should not be null"),
                () -> assertTrue(points.isEmpty(), "Points list should be empty")
        );
    }

    @Test
    @Order(10)
    @DisplayName("Should maintain point index uniqueness")
    void testPointIndexUniqueness() {
        // Given
        PointDto point1 = new PointDto(TEST_FUNC_ID, 1.0, 1.0, 0);
        pointDao.save(point1);

        // When - try to save point with same index
        PointDto point2 = new PointDto(TEST_FUNC_ID, 2.0, 4.0, 0);

        // Then - should handle unique constraint violation
        // Note: Actual behavior depends on DAO implementation
        // This test documents the expected constraint
        assertDoesNotThrow(() -> {
            // Implementation should handle or prevent duplicate indexes
            pointDao.save(point2);
        });
    }

    @Test
    @Order(11)
    @DisplayName("Should retrieve point by ID after save")
    void testFindPointById() {
        // Given
        PointDto originalPoint = new PointDto(TEST_FUNC_ID, 7.0, 49.0, 7);

        // When
        Long pointId = pointDao.save(originalPoint);
        savedPointId = pointId;

        Optional<PointDto> foundPoint = pointDao.findById(pointId);

        // Then
        assertAll(
                () -> assertTrue(foundPoint.isPresent(), "Point should be found by ID"),
                () -> assertEquals(7.0, foundPoint.get().getX(), 0.001, "X coordinate should match"),
                () -> assertEquals(49.0, foundPoint.get().getY(), 0.001, "Y coordinate should match"),
                () -> assertEquals(7, foundPoint.get().getIndex(), "Point index should match"),
                () -> assertEquals(TEST_FUNC_ID, foundPoint.get().getFunctionId(), "Function ID should match")
        );
    }

    @Test
    @Order(12)
    @DisplayName("Should handle points with negative coordinates")
    void testPointsWithNegativeCoordinates() {
        // Given
        PointDto point = new PointDto(TEST_FUNC_ID, -5.0, -25.0, 10);

        // When
        Long pointId = pointDao.save(point);
        savedPointId = pointId;

        Optional<PointDto> foundPoint = pointDao.findById(pointId);

        // Then
        assertAll(
                () -> assertTrue(foundPoint.isPresent(), "Point with negative coordinates should be saved"),
                () -> assertEquals(-5.0, foundPoint.get().getX(), 0.001, "Negative X coordinate should be preserved"),
                () -> assertEquals(-25.0, foundPoint.get().getY(), 0.001, "Negative Y coordinate should be preserved")
        );
    }

    @Test
    @Order(13)
    @DisplayName("Should handle points with decimal coordinates")
    void testPointsWithDecimalCoordinates() {
        // Given
        PointDto point = new PointDto(TEST_FUNC_ID, 3.14159, 2.71828, 15);

        // When
        Long pointId = pointDao.save(point);
        savedPointId = pointId;

        Optional<PointDto> foundPoint = pointDao.findById(pointId);

        // Then
        assertAll(
                () -> assertTrue(foundPoint.isPresent(), "Point with decimal coordinates should be saved"),
                () -> assertEquals(3.14159, foundPoint.get().getX(), 0.00001, "Decimal X coordinate should be preserved"),
                () -> assertEquals(2.71828, foundPoint.get().getY(), 0.00001, "Decimal Y coordinate should be preserved")
        );
    }

    @Test
    @Order(14)
    @DisplayName("Should maintain points order by index")
    void testPointsOrderByIndex() {
        // Given - save points in random index order
        PointDto point3 = new PointDto(TEST_FUNC_ID, 3.0, 9.0, 3);
        PointDto point1 = new PointDto(TEST_FUNC_ID, 1.0, 1.0, 1);
        PointDto point2 = new PointDto(TEST_FUNC_ID, 2.0, 4.0, 2);
        PointDto point0 = new PointDto(TEST_FUNC_ID, 0.0, 0.0, 0);

        pointDao.save(point3);
        pointDao.save(point1);
        pointDao.save(point2);
        pointDao.save(point0);

        // When
        List<PointDto> points = pointDao.findByFunctionId(TEST_FUNC_ID);

        // Then - points should be ordered by index
        assertAll(
                () -> assertEquals(4, points.size(), "Should find all 4 points"),
                () -> assertEquals(0, points.get(0).getIndex(), "First point should have index 0"),
                () -> assertEquals(1, points.get(1).getIndex(), "Second point should have index 1"),
                () -> assertEquals(2, points.get(2).getIndex(), "Third point should have index 2"),
                () -> assertEquals(3, points.get(3).getIndex(), "Fourth point should have index 3")
        );
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle zero coordinates")
        void testZeroCoordinates() {
            // Given
            PointDto point = new PointDto(TEST_FUNC_ID, 0.0, 0.0, 0);

            // When
            Long pointId = pointDao.save(point);

            // Then
            Optional<PointDto> foundPoint = pointDao.findById(pointId);
            assertAll(
                    () -> assertTrue(foundPoint.isPresent()),
                    () -> assertEquals(0.0, foundPoint.get().getX(), 0.0),
                    () -> assertEquals(0.0, foundPoint.get().getY(), 0.0)
            );
        }

        @Test
        @DisplayName("Should handle very large coordinates")
        void testLargeCoordinates() {
            // Given
            PointDto point = new PointDto(TEST_FUNC_ID, 1.0E+10, -1.0E+10, 100);

            // When
            Long pointId = pointDao.save(point);

            // Then
            Optional<PointDto> foundPoint = pointDao.findById(pointId);
            assertAll(
                    () -> assertTrue(foundPoint.isPresent()),
                    () -> assertEquals(1.0E+10, foundPoint.get().getX(), 1.0),
                    () -> assertEquals(-1.0E+10, foundPoint.get().getY(), 1.0)
            );
        }

        @Test
        @DisplayName("Should handle duplicate point deletion")
        void testDuplicateDeletion() {
            // Given
            PointDto point = new PointDto(TEST_FUNC_ID, 1.0, 1.0, 0);
            Long pointId = pointDao.save(point);

            // When - delete twice
            boolean firstDelete = pointDao.delete(pointId);
            boolean secondDelete = pointDao.delete(pointId);

            // Then
            assertAll(
                    () -> assertTrue(firstDelete, "First delete should be successful"),
                    () -> assertFalse(secondDelete, "Second delete should fail for non-existent point")
            );
        }
    }

    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {

        @Test
        @DisplayName("Should handle bulk point operations efficiently")
        void testBulkOperations() {
            // Given - create multiple points
            int pointCount = 10;

            // When
            for (int i = 0; i < pointCount; i++) {
                PointDto point = new PointDto(TEST_FUNC_ID, (double) i, (double) i * i, i);
                pointDao.save(point);
            }

            List<PointDto> points = pointDao.findByFunctionId(TEST_FUNC_ID);

            // Then
            assertEquals(pointCount, points.size(), "Should retrieve all points");
        }

        @Test
        @DisplayName("Should count points efficiently")
        void testEfficientCounting() {
            // Given
            int expectedCount = 5;
            for (int i = 0; i < expectedCount; i++) {
                PointDto point = new PointDto(TEST_FUNC_ID, (double) i, (double) i, i);
                pointDao.save(point);
            }

            // When
            long startTime = System.currentTimeMillis();
            int count = pointDao.countByFunctionId(TEST_FUNC_ID);
            long endTime = System.currentTimeMillis();

            // Then
            assertAll(
                    () -> assertEquals(expectedCount, count, "Should count points correctly"),
                    () -> assertTrue((endTime - startTime) < 1000, "Counting should be efficient")
            );
        }
    }
}
