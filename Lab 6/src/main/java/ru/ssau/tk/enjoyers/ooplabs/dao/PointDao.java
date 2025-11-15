package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import java.util.List;
import java.util.Optional;

public interface PointDao {
    Optional<PointDto> findById(Long id);
    List<PointDto> findByFunctionId(Long functionId);
    Optional<PointDto> findByFunctionIdAndIndex(Long functionId, Integer index);
    Long save(PointDto point);
    boolean update(PointDto point);
    boolean delete(Long id);
    boolean deleteByFunctionId(Long functionId);
    int countByFunctionId(Long functionId);
}
