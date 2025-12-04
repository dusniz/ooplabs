package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.entity.Function;
import ru.ssau.tk.enjoyers.ooplabs.entity.Point;
import java.util.List;
import java.util.Optional;

public interface FunctionDao {
    Optional<Function> findById(Long id);
    List<Function> findByUserId(Long userId);
    List<Function> findByUserIdAndType(Long userId, String type);
    Long save(Function function);
    boolean update(Function function);
    boolean delete(Long id);
    boolean existsById(Long id);

    List<Point> findPointsByFunctionId(Long functionId);
    Optional<Point> findPointByFunctionIdAndIndex(Long functionId, Integer index);
    void savePoints(Long functionId, List<Point> points);
    boolean updatePoint(Long functionId, Point point);
    boolean deletePoint(Long pointId);
    boolean deleteAllPointsByFunctionId(Long functionId);
    int countPointsByFunctionId(Long functionId);
}
