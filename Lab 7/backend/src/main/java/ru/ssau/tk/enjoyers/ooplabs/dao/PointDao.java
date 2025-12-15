package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.entity.Point;
import java.util.List;
import java.util.Optional;

public interface PointDao {
    Optional<Point> findById(Long id);
    List<Point> findByFunctionId(Long functionId);
    Optional<Point> findByFunctionIdAndIndex(Long functionId, Integer index);
    Long save(Point point);
    boolean update(Point point);
    boolean delete(Long id);
    boolean deleteByFunctionId(Long functionId);
    int countByFunctionId(Long functionId);
}
