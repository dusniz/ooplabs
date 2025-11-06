package ru.ssau.tk.enjoyers.ooplabs.services;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Function Service Integration Tests")
class FunctionServiceTest {

    @Autowired
    private FunctionService functionService;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    private Long testUserId = 1L;
    private Function testFunction;

    @BeforeEach
    void setUp() {
        // Cleanup before each test
        functionRepository.deleteAll();
        pointRepository.deleteAll();

        testFunction = new Function(testUserId, "Test Function", "Test Function",
                "TABULATED", 0, "TABULATED_ARRAY");
    }

    @Test
    @DisplayName("Should create function")
    void createFunction() {
        Function savedFunction = functionService.createFunction(testFunction);

        assertAll(
                () -> assertNotNull(savedFunction.getId(), "Function should have ID"),
                () -> assertEquals("Test Function", savedFunction.getName())
        );
    }

    @Test
    @DisplayName("Should get function")
    void getFunction() {
        Function createdFunction = functionService.createFunction(testFunction);

        Optional<Function> foundFunction = functionService.getFunction(createdFunction.getId());

        assertAll(
                () -> assertTrue(foundFunction.isPresent(), "Function should be found")
        );
    }

    @Test
    @DisplayName("Should update function points")
    void updateFunctionPoints() {
        testFunction.setPointCount(2);
        Function createdFunction = functionService.createFunction(testFunction);

        List<Point> newPoints = Arrays.asList(
                new Point(null, 5.0, 25.0, 0),
                new Point(null, 6.0, 36.0, 1)
        );

        // When
        functionService.updateFunctionPoints(createdFunction.getId(), newPoints);

        assertAll(
                () -> assertEquals(2, testFunction.getPointCount()),
                () -> assertEquals(25.0, pointRepository.findByFunctionIdAndIndex(
                        testFunction.getId(), 0).get().getY()),
                () -> assertEquals(36.0, pointRepository.findByFunctionIdAndIndex(
                        testFunction.getId(), 1).get().getY())
        );
    }

    @Test
    @DisplayName("Should count user functions")
    void getUserFunctionCount() {
        // Given
        functionService.createFunction(testFunction);
        functionService.createFunction(testFunction);

        // When
        long count = functionService.getUserFunctionCount(testUserId);

        // Then
        assertEquals(1, count, "Should count 1 functions for user");
    }

    @Test
    @DisplayName("Should not found any points")
    void getFunctionPoints_NotFound() {
        assertEquals(List.of(), functionService.getFunctionPoints(-999999L), "Should not found any points");
    }
}