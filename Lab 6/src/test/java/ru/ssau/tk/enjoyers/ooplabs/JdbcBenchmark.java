package ru.ssau.tk.enjoyers.ooplabs;

import ru.ssau.tk.enjoyers.ooplabs.dao.SearchJdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.SearchCriteria;
import ru.ssau.tk.enjoyers.ooplabs.dto.UserDto;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcFunctionDao;
import ru.ssau.tk.enjoyers.ooplabs.dao.JdbcUserDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
@DisplayName("JDBC Performance Tests")
class JdbcBenchmark {
    private JdbcUserDao userDao;
    private JdbcFunctionDao functionDao;
    private SearchJdbcFunctionDao advancedDao;
    private Long testUserId;
    private static final int LARGE_DATA_SIZE = 1000;

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

        userDao = new JdbcUserDao();
        functionDao = new JdbcFunctionDao();
        advancedDao = new SearchJdbcFunctionDao(functionDao);

        // Создаем тестового пользователя
        UserDto user = new UserDto("perf_test_user_jdbc", "password", Role.USER);
        testUserId = userDao.save(user);
        assertNotNull(testUserId, "Failed to create test user");
    }

    @AfterEach
    void tearDown() {
        // Очистка тестовых данных
        if (testUserId != null) {
            functionDao.findByUserId(testUserId).forEach(func -> functionDao.delete(func.getId()));
            userDao.delete(testUserId);
        }
    }

    @Test
    @Order(1)
    @DisplayName("Performance: Save functions with points")
    void performanceSaveFunctionsWithPoints() {
        List<FunctionDto> functions = DataGenerator.generateFunctionsDto(testUserId, LARGE_DATA_SIZE);

        assertAll(() -> {
            long startTime = System.currentTimeMillis();

            for (FunctionDto function : functions) {
                Long functionId = functionDao.save(function);
                assertNotNull(functionId, "Failed to save function");

                // Генерируем точки для функции
                List<PointDto> points = DataGenerator.generatePointsDto(functionId, 10, 0, 10);
                functionDao.savePoints(functionId, points);
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.printf("JDBC: Saved %d functions with points in %d ms (%.2f ms per function)%n",
                    LARGE_DATA_SIZE, duration, (double) duration / LARGE_DATA_SIZE);

            List<FunctionDto> savedFunctions = functionDao.findByUserId(testUserId);
            assertEquals(LARGE_DATA_SIZE, savedFunctions.size());
        });
    }

    @Test
    @Order(2)
    @DisplayName("Performance: Read functions with points")
    void performanceReadFunctionsWithPoints() {
        List<FunctionDto> functions = DataGenerator.generateFunctionsDto(testUserId, LARGE_DATA_SIZE);
        for (FunctionDto function : functions) {
            Long functionId = functionDao.save(function);
            List<PointDto> points = DataGenerator.generatePointsDto(functionId, 10, 0, 10);
            functionDao.savePoints(functionId, points);
        }

        assertAll(() -> {
            long startTime = System.currentTimeMillis();

            List<FunctionDto> foundFunctions = functionDao.findByUserId(testUserId);
            for (FunctionDto function : foundFunctions) {
                List<PointDto> points = functionDao.findPointsByFunctionId(function.getId());

                assertFalse(points.isEmpty(), "Function should have points");
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            System.out.printf("JDBC: Read %d functions with points in %d ms%n",
                    foundFunctions.size(), duration);
        });
    }

    @Test
    @Order(3)
    @DisplayName("Performance: Bulk points operations")
    void performanceBulkPointsOperations() {
        FunctionDto function = new FunctionDto(testUserId, "Bulk Test Function", "TABULATED",
                "Benchmark function", 0, "TABULATED_ARRAY");
        Long functionId = functionDao.save(function);

        assertAll(() -> {
            List<PointDto> points = DataGenerator.generatePointsDto(functionId, LARGE_DATA_SIZE * 10, 0, 10000);

            long startTime = System.currentTimeMillis();
            functionDao.savePoints(functionId, points);
            long insertTime = System.currentTimeMillis() - startTime;

            startTime = System.currentTimeMillis();
            List<PointDto> readPoints = functionDao.findPointsByFunctionId(functionId);
            long readTime = System.currentTimeMillis() - startTime;

            startTime = System.currentTimeMillis();
            functionDao.deleteAllPointsByFunctionId(functionId);
            long deleteTime = System.currentTimeMillis() - startTime;

            System.out.printf("JDBC Points Operations:%n");
            System.out.printf("  Insert %d points: %d ms%n", LARGE_DATA_SIZE * 10, insertTime);
            System.out.printf("  Read %d points: %d ms%n", readPoints.size(), readTime);
            System.out.printf("  Delete points: %d ms%n", deleteTime);
        });
    }

    @Test
    @Order(4)
    @DisplayName("Search with sorting")
    void testSearchWithSorting() {
        List<FunctionDto> functions = DataGenerator.generateFunctionsDto(testUserId, LARGE_DATA_SIZE / 10);
        for (FunctionDto function : functions) {
            Long functionId = functionDao.save(function);
            List<PointDto> points = DataGenerator.generatePointsDto(functionId, 10, 0, 10);
            functionDao.savePoints(functionId, points);
        }

        List<FunctionDto> ascendingResults = advancedDao.findWithSorting("name", SearchCriteria.SortDirection.ASC);
        List<FunctionDto> descendingResults = advancedDao.findWithSorting("name", SearchCriteria.SortDirection.DESC);

        assertNotNull(ascendingResults);
        assertNotNull(descendingResults);

        if (ascendingResults.size() > 1 && descendingResults.size() > 1) {
            // Проверяем, что сортировка работает
            String firstAsc = ascendingResults.get(0).getName();
            String lastAsc = ascendingResults.get(ascendingResults.size() - 1).getName();
            String firstDesc = descendingResults.get(0).getName();
            String lastDesc = descendingResults.get(descendingResults.size() - 1).getName();

            assertTrue(firstAsc.compareTo(lastAsc) <= 0);
            assertTrue(firstDesc.compareTo(lastDesc) >= 0);
        }
    }

//    @Test
//    @Order(5)
//    @DisplayName("Performance: Concurrent access")
//    @Timeout(value = 60, unit = TimeUnit.SECONDS)
//    void performanceConcurrentAccess() throws InterruptedException {
//        int threadCount = 10;
//        int operationsPerThread = LARGE_DATA_SIZE / threadCount;
//
//        Thread[] threads = new Thread[threadCount];
//        final int[] successfulOperations = new int[threadCount];
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < threadCount; i++) {
//            final int threadIndex = i;
//            threads[i] = new Thread(() -> {
//                try {
//                    for (int j = 0; j < operationsPerThread; j++) {
//                        // Каждый поток работает со своей функцией
//                        FunctionDto function = new FunctionDto(
//                                testUserId,
//                                "Concurrent_Func_" + threadIndex + "_" + j,
//                                "TABULATED", "Benchmark function", 0, "TABULATED_ARRAY"
//                        );
//
//                        Long functionId = functionDao.save(function);
//                        if (functionId != null) {
//                            successfulOperations[threadIndex]++;
//                        }
//                    }
//                } catch (Exception e) {
//                    System.err.println("Thread " + threadIndex + " failed: " + e.getMessage());
//                }
//            });
//            threads[i].start();
//        }
//
//        // Ждем завершения всех потоков
//        for (Thread thread : threads) {
//            thread.join();
//        }
//
//        long endTime = System.currentTimeMillis();
//        long duration = endTime - startTime;
//
//        int totalOperations = 0;
//        for (int count : successfulOperations) {
//            totalOperations += count;
//        }
//
//        System.out.printf("JDBC Concurrent: %d threads, %d total operations in %d ms%n",
//                threadCount, totalOperations, duration);
//
//        assertTrue(totalOperations >= operationsPerThread * threadCount * 0.9,
//                "At least 90% of operations should succeed");
//    }
}
