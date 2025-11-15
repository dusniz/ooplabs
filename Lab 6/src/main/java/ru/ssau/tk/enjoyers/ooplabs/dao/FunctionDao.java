package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import java.util.List;
import java.util.Optional;

public interface FunctionDao {
    Optional<FunctionDto> findById(Long id);
    List<FunctionDto> findByUserId(Long userId);
    List<FunctionDto> findByUserIdAndType(Long userId, String type);
    Long save(FunctionDto function);
    boolean update(FunctionDto function);
    boolean delete(Long id);
    boolean existsById(Long id);

    List<PointDto> findPointsByFunctionId(Long functionId);
    Optional<PointDto> findPointByFunctionIdAndIndex(Long functionId, Integer index);
    void savePoints(Long functionId, List<PointDto> points);
    boolean updatePoint(Long functionId, PointDto point);
    boolean deletePoint(Long pointId);
    boolean deleteAllPointsByFunctionId(Long functionId);
    int countPointsByFunctionId(Long functionId);
}
