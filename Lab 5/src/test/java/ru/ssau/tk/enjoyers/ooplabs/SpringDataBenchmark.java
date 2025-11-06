package ru.ssau.tk.enjoyers.ooplabs;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.UserRepository;
import ru.ssau.tk.enjoyers.ooplabs.services.FunctionService;
import ru.ssau.tk.enjoyers.ooplabs.services.PointService;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Spring Data Performance Tests")
class SpringDataBenchmark {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private PointRepository pointRepository;

    @Autowired
    private FunctionService functionService;

    @Autowired
    private PointService pointService;

    private Long testUserId;
    private static final int LARGE_DATA_SIZE = 10000;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        User user = new User("perf_test_user_spring", "password", Role.USER);
        User savedUser = userRepository.save(user);
        testUserId = savedUser.getId();
    }

    @AfterEach
    void tearDown() {
        // Очистка выполняется автоматически благодаря @Transactional
    }

    @Test
    @DisplayName("Performance: Save functions with points")
    @Timeout(value = 100000, unit = TimeUnit.SECONDS)
    void performanceSaveFunctionsWithPoints() {
        // Given
        AtomicReference<List<Function>> functions = new AtomicReference<>(DataGenerator.generateFunctions(testUserId, LARGE_DATA_SIZE, "TABULATED", "TABULATED_LINKED_LIST"));

        // When & Then
        assertTimeoutPreemptively(Duration.ofSeconds(100000), () -> {
            long startTime = System.currentTimeMillis();

            for (Function function : functions.get())
                functionService.createFunction(function);
            functions.set(functionService.getUserFunctions(testUserId));

            for (Function function : functions.get()) {
                // Генерируем точки для функции
                List<Point> points = DataGenerator.generatePoints(function.getId(), 100, 0, 10);
                for (Point point : points)
                    pointService.createPoint(function.getId(), point.getX(), point.getY(), point.getIndex());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.printf("Spring Data: Saved %d functions with points in %d ms (%.2f ms per function)%n",
                    LARGE_DATA_SIZE, duration, (double) duration / LARGE_DATA_SIZE);

            // Verify
            List<Function> savedFunctions = functionRepository.findByUserId(testUserId);
            assertEquals(LARGE_DATA_SIZE, savedFunctions.size());
        });
    }

    @Test
    @DisplayName("Performance: Read functions with points")
    @Timeout(value = 100000, unit = TimeUnit.SECONDS)
    void performanceReadFunctionsWithPoints() {
        // Given - создаем тестовые данные
        List<Function> functions = DataGenerator.generateFunctions(testUserId, LARGE_DATA_SIZE / 10, "TABULATED", "TABULATED_LINKED_LIST");
        for (Function function : functions) {
            List<Point> points = DataGenerator.generatePoints(function.getId(), 50, 0, 10);
            functionService.createFunction(function);
        }

        // When & Then
        assertTimeoutPreemptively(Duration.ofSeconds(100000), () -> {
            long startTime = System.currentTimeMillis();

            List<Function> foundFunctions = functionRepository.findByUserId(testUserId);
            for (Function function : foundFunctions) {
                List<Point> functionPoints = functionService.getFunctionPoints(function.getId());

                assertFalse(functionPoints.isEmpty(), "Function should have points");
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.printf("Spring Data: Read %d functions with points in %d ms%n",
                    foundFunctions.size(), duration);
        });
    }

    @Test
    @DisplayName("Performance: Bulk points operations")
    @Timeout(value = 100000, unit = TimeUnit.SECONDS)
    void performanceBulkPointsOperations() {
        // Given
        Function function = new Function(testUserId, "Bulk Test Function", "Bulk Test Function", "TABULATED", 100, "TABULATED_LINKED_LIST");
        Function savedFunction = functionRepository.save(function);

        // When & Then
        assertTimeoutPreemptively(Duration.ofSeconds(100000), () -> {
            // Тест массовой вставки точек
            List<Point> points = DataGenerator.generatePoints(
                    savedFunction.getId(), LARGE_DATA_SIZE, 0, 100);

            long startTime = System.currentTimeMillis();
            pointRepository.saveAll(points);
            pointRepository.flush();
            long insertTime = System.currentTimeMillis() - startTime;

            // Тест чтения точек
            startTime = System.currentTimeMillis();
            List<Point> readPoints = pointRepository.findByFunctionIdOrderByIndex(savedFunction.getId());
            long readTime = System.currentTimeMillis() - startTime;

            // Тест удаления точек
            startTime = System.currentTimeMillis();
            pointRepository.deleteByFunctionId(savedFunction.getId());
            pointRepository.flush();
            long deleteTime = System.currentTimeMillis() - startTime;

            System.out.printf("Spring Data Points Operations:%n");
            System.out.printf("  Insert %d points: %d ms%n", LARGE_DATA_SIZE, insertTime);
            System.out.printf("  Read %d points: %d ms%n", readPoints.size(), readTime);
            System.out.printf("  Delete points: %d ms%n", deleteTime);
        });
    }

    @Test
    @DisplayName("Performance: Query methods")
    @Timeout(value = 100000, unit = TimeUnit.SECONDS)
    void performanceQueryMethods() {
        // Given - создаем разнообразные данные
        for (int i = 0; i < LARGE_DATA_SIZE / 10; i++) {
            Function function = new Function(
                    testUserId,
                    "QueryTest_" + i,
                    "QueryTest function",
                    "TABULATED",
                    i % 100,
                    i % 2 == 0 ? "TABULATED_ARRAY" : "TABULATED_LINKED_LIST");
            functionRepository.save(function);
        }
        functionRepository.flush();

        // When & Then
        assertTimeoutPreemptively(Duration.ofSeconds(100000), () -> {
            long startTime = System.currentTimeMillis();

            // Тестируем различные запросы
            List<Function> byType = functionRepository.findByUserIdAndType(testUserId, "TABULATED_ARRAY");
            List<Function> byName = functionRepository.findByNameContainingIgnoreCase("QueryTest");
            long count = functionRepository.countByUserIdAndType(testUserId, "TABULATED_LINKED_LIST");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.printf("Spring Data Queries:%n");
            System.out.printf("  Found %d by type, %d by name, with points count: %d%n",
                    byType.size(), byName.size(), count);
            System.out.printf("  All queries completed in %d ms%n", duration);

            assertAll(
                    () -> assertFalse(byType.isEmpty()),
                    () -> assertFalse(byName.isEmpty()),
                    () -> assertTrue(count >= 0)
            );
        });
    }
}
