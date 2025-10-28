package ru.ssau.tk.enjoyers.ooplabs.repositories;

import ru.ssau.tk.enjoyers.ooplabs.Role;
import ru.ssau.tk.enjoyers.ooplabs.models.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Function Repository Tests")
class FunctionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FunctionRepository functionRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;
    private Function arrayFunction;
    private Function linkedListFunction;

    @BeforeEach
    void setUp() {
        // Создаем тестового пользователя
        testUser = new User("functionuser", "password", Role.USER);
        entityManager.persistAndFlush(testUser);

        // Создаем тестовые функции
        arrayFunction = new Function("Quadratic Function", "TABULATED_ARRAY");
        arrayFunction.setDescription("Function with array implementation");
        arrayFunction.setPointCount(10);

        linkedListFunction = new Function("Sine Wave", "TABULATED_LINKED_LIST");
        linkedListFunction.setDescription("Function with linked list implementation");
        linkedListFunction.setPointCount(5);

        entityManager.persistAndFlush(arrayFunction);
        entityManager.persistAndFlush(linkedListFunction);
    }

    @AfterEach
    void tearDown() {
        entityManager.clear();
    }

    @Test
    @DisplayName("Should find functions by user ID")
    void findByUserId() {
        // When
        List<Function> functions = functionRepository.findByUserId(testUser.getId());

        // Then
        assertAll(
                () -> assertEquals(2, functions.size(), "Should find 2 functions for user"),
                () -> assertTrue(functions.stream().allMatch(f -> f.getUserId().equals(testUser.getId())))
        );
    }

    @Test
    @DisplayName("Should find functions by user ID and type")
    void findByUserIdAndType() {
        // When
        List<Function> arrayFunctions = functionRepository.findByUserIdAndType(
                testUser.getId(), "TABULATED_ARRAY");
        List<Function> linkedListFunctions = functionRepository.findByUserIdAndType(
                testUser.getId(), "TABULATED_LINKED_LIST");

        // Then
        assertAll(
                () -> assertEquals(1, arrayFunctions.size(), "Should find 1 array function"),
                () -> assertEquals(1, linkedListFunctions.size(), "Should find 1 linked list function"),
                () -> assertEquals("TABULATED_ARRAY", arrayFunctions.get(0).getType()),
                () -> assertEquals("TABULATED_LINKED_LIST", linkedListFunctions.get(0).getType())
        );
    }

    @Test
    @DisplayName("Should find functions by name containing")
    void findByNameContainingIgnoreCase() {
        // When
        List<Function> quadraticFunctions = functionRepository.findByNameContainingIgnoreCase("quadratic");
        List<Function> sineFunctions = functionRepository.findByNameContainingIgnoreCase("sine");

        // Then
        assertAll(
                () -> assertEquals(1, quadraticFunctions.size(), "Should find quadratic function"),
                () -> assertEquals(1, sineFunctions.size(), "Should find sine function"),
                () -> assertTrue(quadraticFunctions.get(0).getName().toLowerCase().contains("quadratic")),
                () -> assertTrue(sineFunctions.get(0).getName().toLowerCase().contains("sine"))
        );
    }

    @Test
    @DisplayName("Should count functions by user ID")
    void countByUserId() {
        // When
        long count = functionRepository.countByUserId(testUser.getId());

        // Then
        assertEquals(2, count, "Should count 2 functions for user");
    }

    @Test
    @DisplayName("Should check if function exists by ID and user ID")
    void existsByIdAndUserId() {
        // When & Then
        assertAll(
                () -> assertTrue(functionRepository.existsByIdAndUserId(
                        arrayFunction.getId(), testUser.getId())),
                () -> assertFalse(functionRepository.existsByIdAndUserId(
                        999999L, testUser.getId()))
        );
    }

    @Test
    @DisplayName("Should count functions by user ID and type")
    void countByUserIdAndType() {
        long arrayCount = functionRepository.countByUserIdAndType(testUser.getId(), "TABULATED_ARRAY");
        long linkedListCount = functionRepository.countByUserIdAndType(testUser.getId(), "TABULATED_LINKED_LIST");

        assertAll(
                () -> assertEquals(1, arrayCount, "Should count 1 array function"),
                () -> assertEquals(1, linkedListCount, "Should count 1 linked list function")
        );
    }

    @Test
    @DisplayName("Should save new function")
    void save() {
        // Given
        Function newFunction = new Function("New Function", "TABULATED_ARRAY");
        newFunction.setDescription("New test function");
        newFunction.setPointCount(3);

        // When
        Function savedFunction = functionRepository.save(newFunction);

        // Then
        assertAll(
                () -> assertNotNull(savedFunction.getId(), "Saved function should have ID"),
                () -> assertEquals("New Function", savedFunction.getName()),
                () -> assertEquals(testUser.getId(), savedFunction.getUserId()),
                () -> assertEquals(3, functionRepository.countByUserId(testUser.getId()),
                        "Should have 3 functions after save")
        );
    }

    @Test
    @DisplayName("Should update function")
    void update() {
        // Given
        Function function = functionRepository.findById(arrayFunction.getId()).get();

        // When
        function.setName("Updated Function Name");
        function.setPointCount(20);
        Function updatedFunction = functionRepository.save(function);

        // Then
        assertAll(
                () -> assertEquals("Updated Function Name", updatedFunction.getName()),
                () -> assertEquals(20, updatedFunction.getPointCount())
        );
    }

    @Test
    @DisplayName("Should delete function")
    void delete() {
        // Given
        Function function = functionRepository.findById(arrayFunction.getId()).get();

        // When
        functionRepository.delete(function);

        // Then
        assertAll(
                () -> assertFalse(functionRepository.existsById(arrayFunction.getId())),
                () -> assertEquals(1, functionRepository.countByUserId(testUser.getId()))
        );
    }

    @Test
    @DisplayName("Should find function by ID")
    void findById() {
        // When
        Optional<Function> found = functionRepository.findById(arrayFunction.getId());

        // Then
        assertAll(
                () -> assertTrue(found.isPresent(), "Function should be found by ID"),
                () -> assertEquals(arrayFunction.getName(), found.get().getName()),
                () -> assertEquals(arrayFunction.getType(), found.get().getType())
        );
    }
}
