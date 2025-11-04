package ru.ssau.tk.enjoyers.ooplabs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {

    List<Point> findByFunctionIdOrderByIndex(Long functionId);

    Optional<Point> findByFunctionIdAndIndex(Long functionId, Integer index);

    List<Point> findByFunctionIdAndXBetween(Long functionId, Double a, Double b);

    List<Point> findByFunctionIdAndYGreaterThan(Long functionId, Double y0);

    @Modifying
    @Query("DELETE FROM Point p WHERE p.functionId = :functionId")
    void deleteByFunctionId(@Param("functionId") Long functionId);

    long countByFunctionId(Long functionId);

    boolean existsByFunctionIdAndIndex(Long functionId, Integer index);
}