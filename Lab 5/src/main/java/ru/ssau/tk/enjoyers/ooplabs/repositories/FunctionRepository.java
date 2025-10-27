package ru.ssau.tk.enjoyers.ooplabs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.enjoyers.ooplabs.models.Function;

import java.util.List;

@Repository
public interface FunctionRepository extends JpaRepository<Function, Long> {

    List<Function> findByUserId(Long userId);

    List<Function> findByUserIdAndType(Long userId, String type);

    List<Function> findByNameContainingIgnoreCase(String name);

    long countByUserId(Long userId);

    boolean existsByIdAndUserId(Long id, Long userId);
}
