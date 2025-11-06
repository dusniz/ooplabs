package ru.ssau.tk.enjoyers.ooplabs;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.entities.User;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.UserRepository;
import ru.ssau.tk.enjoyers.ooplabs.services.AdvancedSearchService;
import ru.ssau.tk.enjoyers.ooplabs.services.FunctionService;
import ru.ssau.tk.enjoyers.ooplabs.services.PointService;
import ru.ssau.tk.enjoyers.ooplabs.services.UserService;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
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

    @Autowired
    private UserService userService;
    
    @Autowired
    private AdvancedSearchService searchService;

    private User user;
    private static final int LARGE_DATA_SIZE = 1000;
    private final int timeout = 100000;

    @BeforeEach
    void setUp() {
        user = new User("perf_test_user_spring", "password", Role.USER);
        User savedUser = userRepository.save(user);
        user = savedUser;
    }

    @AfterEach
    void tearDown() {
        pointRepository.deleteAll();
        functionRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Performance: Save functions with points")
    void performanceSaveFunctionsWithPoints() {
        assertAll(() -> {
        AtomicReference<List<Function>> functions = new AtomicReference<>(DataGenerator.generateFunctions(user.getId(), LARGE_DATA_SIZE, "TABULATED", "TABULATED_LINKED_LIST"));
            long startTime = System.currentTimeMillis();

            for (Function function : functions.get())
                functionService.createFunction(function);
            functions.set(functionService.getUserFunctions(user.getId()));

            for (Function function : functions.get()) {
                // Генерируем точки для функции
                List<Point> points = DataGenerator.generatePoints(function.getId(), 10, 0, 1);
                for (Point point : points)
                    pointService.createPoint(function.getId(), point.getX(), point.getY(), point.getIndex());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.printf("Spring Data: Saved %d functions with points in %d ms (%.2f ms per function)%n",
                    LARGE_DATA_SIZE, duration, (double) duration / LARGE_DATA_SIZE);

            List<Function> savedFunctions = functionRepository.findByUserId(user.getId());
            assertEquals(LARGE_DATA_SIZE, savedFunctions.size());
        });
    }

    @Test
    @DisplayName("Performance: Read functions with points")
    void performanceReadFunctionsWithPoints() {
        assertTimeoutPreemptively(Duration.ofSeconds(timeout), () -> {
        List<Function> functions = DataGenerator.generateFunctions(user.getId(), LARGE_DATA_SIZE, "TABULATED", "TABULATED_LINKED_LIST");
        for (Function function : functions) {
            List<Point> points = DataGenerator.generatePoints(function.getId(), 10, 0, 1);
            functionService.createFunction(function);
            for (Point point : points)
                pointService.createPoint(function.getId(), point.getX(), point.getY(), point.getIndex());
        }
            long startTime = System.currentTimeMillis();

            List<Function> foundFunctions = functionRepository.findByUserId(user.getId());
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
    void performanceBulkPointsOperations() {
        assertTimeoutPreemptively(Duration.ofSeconds(timeout), () -> {
        Function function = new Function(user.getId(), "Bulk Test Function", "Bulk Test Function", "TABULATED", 1000, "TABULATED_LINKED_LIST");
        Function savedFunction = functionRepository.save(function);
            // Тест массовой вставки точек
            List<Point> points = DataGenerator.generatePoints(
                    savedFunction.getId(), 1000, 0, 100);

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
    void performanceQueryMethods() {
        assertTimeoutPreemptively(Duration.ofSeconds(timeout), () -> {
        for (int i = 0; i < LARGE_DATA_SIZE; i++) {
            Function function = new Function(
                    user.getId(),
                    "QueryTest_" + i,
                    "QueryTest function",
                    "TABULATED",
                    1,
                    "TABULATED_ARRAY");
            Function savedFunction = functionRepository.save(function);
            System.out.println(savedFunction.getId());
        }
        functionRepository.flush();

            long startTime = System.currentTimeMillis();

            // Тестируем различные запросы
            List<Function> byType = functionRepository.findByUserIdAndType(user.getId(), "TABULATED");
            List<Function> byName = functionRepository.findByNameContainingIgnoreCase("QueryTest");
            long count = functionRepository.countByUserIdAndType(user.getId(), "TABULATED");

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

    @Test
    @DisplayName("Should search with sorting")
    void searchWithSorting() {
        List<Function> functions = DataGenerator.generateFunctions(user.getId(), 100, "TABULATED", "TABULATED_LINKED_LIST");
        for (Function function : functions)
            functionService.createFunction(function);
        functions = functionService.getUserFunctions(user.getId());
        for (Function function : functions) {
            // Генерируем точки для функции
            List<Point> points = DataGenerator.generatePoints(function.getId(), 10, 0, 1);
            for (Point point : points)
                pointService.createPoint(function.getId(), point.getX(), point.getY(), point.getIndex());
        }


        String sortField = "name";
        List<Function> asc = searchService.searchWithSorting(user.getId(), sortField, true);
        List<Function> desc = searchService.searchWithSorting(user.getId(), sortField, false);
        assertAll(
                () -> assertEquals(100, asc.size(), "Should find all functions"),
                () -> assertEquals(100, desc.size(), "Should find all functions"),
                () -> assertTrue(asc.getFirst().getName().compareTo(asc.get(2).getName()) <= 0,
                        String.format("%s should be in ascending order", sortField)),
                () -> assertTrue(desc.getFirst().getPointCount() >= desc.get(2).getPointCount(),
                        String.format("%s should be in descending order", sortField))
        );
    }

}
