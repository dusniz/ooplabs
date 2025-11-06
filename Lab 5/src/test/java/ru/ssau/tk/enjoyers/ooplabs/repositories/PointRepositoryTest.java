package ru.ssau.tk.enjoyers.ooplabs.repositories;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Point Repository Tests")
class PointRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionRepository functionRepository;

    private User testUser;
    private Function testFunction;
    private Point point1;
    private Point point2;
    private Point point3;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User("pointuser", "password", Role.USER);
        entityManager.persistAndFlush(testUser);

        // Создаем тестовую функцию
        testFunction = new Function(testUser.getId(), "Test Function", "Test Function" 
                ,"TABULATED", 3,
                "TABULATED_ARRAY");
        entityManager.persistAndFlush(testFunction);

        // Создаем тестовые точки
        point1 = new Point(testFunction.getId(), 0.0, 0.0, 0);
        point2 = new Point(testFunction.getId(), 1.0, 1.0, 1);
        point3 = new Point(testFunction.getId(), 2.0, 4.0, 2);

        entityManager.persistAndFlush(point1);
        entityManager.persistAndFlush(point2);
        entityManager.persistAndFlush(point3);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find points by function ID ordered by index")
    void findByFunctionIdOrderByIndex() {
        // When
        List<Point> points = pointRepository.findByFunctionIdOrderByIndex(testFunction.getId());

        // Then
        assertAll(
                () -> assertEquals(3, points.size(), "Should find 3 points"),
                () -> assertEquals(0, points.get(0).getIndex(), "First point should have index 0"),
                () -> assertEquals(1, points.get(1).getIndex(), "Second point should have index 1"),
                () -> assertEquals(2, points.get(2).getIndex(), "Third point should have index 2"),
                () -> assertEquals(0.0, points.get(0).getX(), 0.001),
                () -> assertEquals(4.0, points.get(2).getY(), 0.001)
        );
    }

    @Test
    @DisplayName("Should find point by function ID and index")
    void findByFunctionIdAndIndex() {
        // When
        Optional<Point> foundPoint = pointRepository.findByFunctionIdAndIndex(
                testFunction.getId(), 1);

        // Then
        assertAll(
                () -> assertTrue(foundPoint.isPresent(), "Point should be found"),
                () -> assertEquals(1.0, foundPoint.get().getX(), 0.001),
                () -> assertEquals(1.0, foundPoint.get().getY(), 0.001),
                () -> assertEquals(1, foundPoint.get().getIndex())
        );
    }

    @Test
    @DisplayName("Should find points by function ID and X range")
    void findByFunctionIdAndXBetween() {
        // When
        List<Point> points = pointRepository.findByFunctionIdAndXBetween(
                testFunction.getId(), 0.5, 1.5);

        // Then
        assertAll(
                () -> assertEquals(1, points.size(), "Should find 1 point in X range [0.5, 1.5]"),
                () -> assertEquals(1.0, points.get(0).getX(), 0.001)
        );
    }

    @Test
    @DisplayName("Should find points by function ID and Y greater than")
    void findByFunctionIdAndYGreaterThan() {
        // When
        List<Point> points = pointRepository.findByFunctionIdAndYGreaterThan(
                testFunction.getId(), 0.5);

        // Then
        assertAll(
                () -> assertEquals(2, points.size(), "Should find 2 points with Y > 0.5"),
                () -> assertTrue(points.stream().allMatch(p -> p.getY() > 0.5))
        );
    }

    @Test
    @DisplayName("Should delete points by function ID")
    void deleteByFunctionId() {
        // When
        pointRepository.deleteByFunctionId(testFunction.getId());

        // Then
        List<Point> pointsAfterDelete = pointRepository.findByFunctionIdOrderByIndex(testFunction.getId());
        assertTrue(pointsAfterDelete.isEmpty(), "All points should be deleted");
    }

    @Test
    @DisplayName("Should count points by function ID")
    void countByFunctionId() {
        // When
        long count = pointRepository.countByFunctionId(testFunction.getId());

        // Then
        assertEquals(3, count, "Should count 3 points");
    }

    @Test
    @DisplayName("Should check if point exists by function ID and index")
    void existsByFunctionIdAndIndex() {
        // When & Then
        assertAll(
                () -> assertTrue(pointRepository.existsByFunctionIdAndIndex(testFunction.getId(), 0)),
                () -> assertTrue(pointRepository.existsByFunctionIdAndIndex(testFunction.getId(), 1)),
                () -> assertFalse(pointRepository.existsByFunctionIdAndIndex(testFunction.getId(), 999))
        );
    }

    @Test
    @DisplayName("Should save new point")
    void save() {
        // Given
        Point newPoint = new Point(testFunction.getId(), 3.0, 9.0, 3);

        // When
        Point savedPoint = pointRepository.save(newPoint);

        // Then
        assertAll(
                () -> assertNotNull(savedPoint.getId(), "Saved point should have ID"),
                () -> assertEquals(3.0, savedPoint.getX(), 0.001),
                () -> assertEquals(9.0, savedPoint.getY(), 0.001),
                () -> assertEquals(3, savedPoint.getIndex()),
                () -> assertEquals(testFunction.getId(), savedPoint.getFunctionId()),
                () -> assertEquals(4, pointRepository.countByFunctionId(testFunction.getId()))
        );
    }

    @Test
    @DisplayName("Should update point")
    void update() {
        // Given
        Point point = pointRepository.findById(point1.getId()).get();

        // When
        point.setX(5.0);
        point.setY(25.0);
        Point updatedPoint = pointRepository.save(point);

        // Then
        assertAll(
                () -> assertEquals(5.0, updatedPoint.getX(), 0.001),
                () -> assertEquals(25.0, updatedPoint.getY(), 0.001)
        );
    }

    @Test
    @DisplayName("Should delete point by ID")
    void deleteById() {
        // Given
        Point point = pointRepository.findById(point1.getId()).get();

        // When
        pointRepository.delete(point);

        // Then
        assertAll(
                () -> assertFalse(pointRepository.existsById(point1.getId())),
                () -> assertEquals(2, pointRepository.countByFunctionId(testFunction.getId()))
        );
    }

    @Test
    @DisplayName("Should find point by ID")
    void findById() {
        // When
        Optional<Point> found = pointRepository.findById(point2.getId());

        // Then
        assertAll(
                () -> assertTrue(found.isPresent(), "Point should be found by ID"),
                () -> assertEquals(point2.getX(), found.get().getX(), 0.001),
                () -> assertEquals(point2.getY(), found.get().getY(), 0.001),
                () -> assertEquals(point2.getIndex(), found.get().getIndex())
        );
    }

    @Test
    @DisplayName("Should handle empty results for non-existent function")
    void findByFunctionId_NonExistent() {
        // When
        List<Point> points = pointRepository.findByFunctionIdOrderByIndex(999999L);

        // Then
        assertTrue(points.isEmpty(), "Should return empty list for non-existent function");
    }
}