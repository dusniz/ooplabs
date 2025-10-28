package ru.ssau.tk.enjoyers.ooplabs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.enjoyers.ooplabs.models.Function;

import java.util.List;

@Repository
public interface FunctionSearchRepository extends JpaRepository<Function, Long>,
        JpaSpecificationExecutor<Function> {

    // Одиночный поиск
    List<Function> findByNameContaining(String name);

    // Множественный поиск
    @Query("SELECT f FROM Function f WHERE f.name LIKE %:name% OR f.description LIKE %:description%")
    List<Function> findByNameOrDescriptionContaining(@Param("name") String name,
                                                           @Param("description") String description);

    // Поиск с сортировкой
    List<Function> findByUserIdOrderByNameAsc(Long userId);
    List<Function> findByUserIdOrderByPointsCountDesc(Long userId);
    List<Function> findByUserIdOrderByCreatedAtDesc(Long userId);

    // Поиск с пагинацией
    Page<Function> findByUserId(Long userId, Pageable pageable);

    // Поиск по диапазону
    List<Function> findByPointsCountBetween(Integer minPoints, Integer maxPoints);

    // Поиск по нескольким значениям (IN)
    List<Function> findByTypeIn(List<String> types);

    // Поиск с агрегацией
    @Query("SELECT f.type, COUNT(f) FROM Function f WHERE f.userId = :userId GROUP BY f.type")
    List<Object[]> countFunctionsByType(@Param("userId") Long userId);

    // Рекурсивный поиск (если бы была иерархия)
    @Query(value = "WITH RECURSIVE user_hierarchy AS (" +
            "  SELECT id FROM users WHERE id = :startUserId " +
            "  UNION ALL " +
            "  SELECT u.id FROM users u " +
            "  INNER JOIN user_hierarchy uh ON u.manager_id = uh.id" +
            ") SELECT f.* FROM functions f " +
            "WHERE f.user_id IN (SELECT id FROM user_hierarchy) " +
            "AND f.name LIKE %:namePattern%",
            nativeQuery = true)
    List<Function> findInUserHierarchy(@Param("startUserId") Long startUserId,
                                             @Param("namePattern") String namePattern);
}