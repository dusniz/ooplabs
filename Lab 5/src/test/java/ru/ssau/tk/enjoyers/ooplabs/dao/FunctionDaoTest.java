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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
class FunctionDaoTest {
    private JdbcFunctionDao functionDao;
    private static final Long TEST_FUNC_ID = 1L;
    private static final Long TEST_USER_ID = 1L;
    private Long savedFunctionId;

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

        functionDao = new JdbcFunctionDao();
        cleanupTestFunctions();
    }

    @AfterEach
    void tearDown() {
        cleanupTestFunctions();
    }

    private void cleanupTestFunctions() {
        if (savedFunctionId != null) {
            functionDao.delete(savedFunctionId);
        }
        // Дополнительная очистка по имени
        functionDao.findByUserId(TEST_USER_ID).stream()
                .filter(f -> f.getName().contains("Test Function"))
                .forEach(f -> functionDao.delete(f.getId()));
    }

    @Test
    @Order(1)
    @DisplayName("Should save and find function with points")
    void testSaveAndFindFunctionWithPoints() {
        // Given
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Test Function", "TABULATED",
                "", 0 , "TABULATED_ARRAY");

        // When
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        // Create and save points
        Long TEST_POINT_ID = 1L;
        List<PointDto> points = Arrays.asList(
                new PointDto(TEST_POINT_ID++, functionId, 0.0, 0.0, 0),
                new PointDto(TEST_POINT_ID++, functionId, 2.5, 6.25, 1),
                new PointDto(TEST_POINT_ID, functionId, 5.0, 25.0, 2)
        );
        functionDao.savePoints(functionId, points);

        // Then
        assertNotNull(functionId, "Function ID should not be null");

        Optional<FunctionDto> foundFunction = functionDao.findById(functionId);
        List<PointDto> foundPoints = functionDao.findPointsByFunctionId(functionId);

        assertAll(
                () -> assertTrue(foundFunction.isPresent(), "Function should be found"),
                () -> assertEquals("Test Function", foundFunction.get().getName()),
                () -> assertEquals(3, foundPoints.size(), "Should find 3 points"),
                () -> assertEquals(6.25, foundPoints.get(1).getY(), 0.001)
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should find functions by user ID")
    void testFindByUserId() {
        // Given
        FunctionDto function1 = new FunctionDto(TEST_USER_ID, "Function 1", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        FunctionDto function2 = new FunctionDto(TEST_USER_ID, "Function 2", "TABULATED",
                "", 0, "TABULATED_LINKED_LIST");

        functionDao.save(function1);
        functionDao.save(function2);

        List<FunctionDto> functions = functionDao.findByUserId(TEST_USER_ID);

        assertAll(
                () -> assertTrue(functions.size() >= 2, "Should find at least 2 functions"),
                () -> assertTrue(functions.stream().allMatch(f -> f.getUserId().equals(TEST_USER_ID)),
                        "All functions should belong to test user")
        );
    }

    @Test
    @Order(2)
    @DisplayName("Should find functions by user ID and function type")
    void testFindByUserIdAndType() {
        // Given
        FunctionDto function1 = new FunctionDto(TEST_USER_ID, "Function 1", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        FunctionDto function2 = new FunctionDto(TEST_USER_ID, "Function 2", "TABULATED",
                "", 0, "TABULATED_LINKED_LIST");

        functionDao.save(function1);
        functionDao.save(function2);

        List<FunctionDto> functions = functionDao.findByUserIdAndType(TEST_USER_ID, "TABULATED");

        assertAll(
                () -> assertTrue(functions.size() >= 2, "Should find at least 2 functions"),
                () -> assertTrue(functions.stream().allMatch(f -> f.getUserId().equals(TEST_USER_ID)),
                        "All functions should belong to test user")
        );
    }

    @Test
    @Order(3)
    @DisplayName("Should update function metadata")
    void testUpdateFunction() {
        // Given
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Update Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        // When
        FunctionDto functionToUpdate = new FunctionDto(functionId, TEST_USER_ID, "Updated Name",
                "TABULATED", "Updated Description", 0, "TABULATED_ARRAY");
        boolean updateResult = functionDao.update(functionToUpdate);

        // Then
        assertTrue(updateResult, "Update should be successful");

        Optional<FunctionDto> updatedFunction = functionDao.findById(functionId);
        assertAll(
                () -> assertTrue(updatedFunction.isPresent()),
                () -> assertEquals("Updated Name", updatedFunction.get().getName()),
                () -> assertEquals("Updated Description", updatedFunction.get().getDescription())
        );
    }

    @Test
    @Order(4)
    @DisplayName("Should delete function and its points")
    void testDeleteFunction() {
        // Given
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Delete Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);

        // Add points
        Long TEST_POINT_ID = 1L;
        List<PointDto> points = Arrays.asList(
                new PointDto(TEST_POINT_ID++, functionId, 1.0, 1.0, 0),
                new PointDto(TEST_POINT_ID, functionId, 2.0, 4.0, 1)
        );
        functionDao.savePoints(functionId, points);

        // When
        boolean deleteResult = functionDao.delete(functionId);

        // Then
        assertAll(
                () -> assertTrue(deleteResult, "Delete should be successful"),
                () -> assertFalse(functionDao.findById(functionId).isPresent(),
                        "Function should be deleted"),
                () -> assertTrue(functionDao.findPointsByFunctionId(functionId).isEmpty(),
                        "Function points should be deleted")
        );
    }

    @Test
    @Order(5)
    @DisplayName("Should count points by function ID")
    void testCountPointsByFunctionId() {
        // Given
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Count Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        Long TEST_POINT_ID = 1L;
        List<PointDto> points = Arrays.asList(
                new PointDto(TEST_POINT_ID++, functionId, 1.0, 1.0, 0),
                new PointDto(TEST_POINT_ID++, functionId, 2.0, 4.0, 1),
                new PointDto(TEST_POINT_ID, functionId, 3.0, 9.0, 2)
        );
        functionDao.savePoints(functionId, points);

        // When
        int pointCount = functionDao.countPointsByFunctionId(functionId);

        // Then
        assertEquals(3, pointCount, "Should count 3 points");
    }

    @Test
    @Order(6)
    @DisplayName("Should find point by function ID and index")
    void testFindPointByFunctionIdAndIndex() {
        // Given
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Point Search Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        Long TEST_POINT_ID = 1L;
        List<PointDto> points = Arrays.asList(
                new PointDto(TEST_POINT_ID++, functionId, 1.0, 1.0, 0),
                new PointDto(TEST_POINT_ID, functionId, 2.0, 4.0, 1)
        );
        functionDao.savePoints(functionId, points);

        // When
        Optional<PointDto> point = functionDao.findPointByFunctionIdAndIndex(functionId, 1);

        // Then
        assertAll(
                () -> assertTrue(point.isPresent(), "Point should be found"),
                () -> assertEquals(2.0, point.get().getX(), 0.001),
                () -> assertEquals(4.0, point.get().getY(), 0.001)
        );
    }

    @Test
    @Order(7)
    @DisplayName("Should update individual point")
    void testUpdatePoint() {
        // Given
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Point Update Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        Long TEST_POINT_ID = 1L;
        List<PointDto> points = Arrays.asList(
                new PointDto(TEST_POINT_ID++, functionId, 1.0, 1.0, 0),
                new PointDto(TEST_POINT_ID, functionId, 2.0, 4.0, 1)
        );
        functionDao.savePoints(functionId, points);

        // When
        PointDto updatedPoint = new PointDto(1L, functionId, 2.0, 8.0, 1); // Изменяем Y
        boolean updateResult = functionDao.updatePoint(functionId, updatedPoint);

        // Then
        assertTrue(updateResult, "Point update should be successful");

        Optional<PointDto> foundPoint = functionDao.findPointByFunctionIdAndIndex(functionId, 1);
        assertAll(
                () -> assertTrue(foundPoint.isPresent()),
                () -> assertEquals(8.0, foundPoint.get().getY(), 0.001, "Y value should be updated")
        );
    }

    @Test
    @Order(8)
    @DisplayName("Should delete individual point")
    void testDeletePoint() {
        FunctionDto function = new FunctionDto(TEST_USER_ID, "Point Update Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        Long TEST_POINT_ID = 1L;
        List<PointDto> points = Arrays.asList(
                new PointDto(TEST_POINT_ID++, functionId, 1.0, 1.0, 0),
                new PointDto(TEST_POINT_ID++, functionId, 2.0, 4.0, 1),
                new PointDto(TEST_POINT_ID, functionId, 3.0, 9.0, 2)
        );
        functionDao.savePoints(functionId, points);

        PointDto pointToDelete = points.get(1);
        boolean Result = functionDao.deletePoint(pointToDelete.getId());
        assertTrue(Result, "Point delete should be successful");

        functionDao.savePoints(functionId, points);
        Result = functionDao.existsById(pointToDelete.getId());
        assertFalse(Result, "Check for existence of deleted point");
    }

    @Test
    @Order(9)
    @DisplayName("Should check function for existence by function ID")
    void testExistsById() {
        FunctionDto function = new FunctionDto(TEST_FUNC_ID, TEST_USER_ID, "Existence by ID Test", "TABULATED",
                "", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);
        savedFunctionId = functionId;

        assertTrue(functionDao.existsById(functionId), "Function should exist");
    }
}
