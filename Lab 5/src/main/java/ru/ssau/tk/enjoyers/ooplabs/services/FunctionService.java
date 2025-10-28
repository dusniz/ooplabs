package ru.ssau.tk.enjoyers.ooplabs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.models.Function;
import ru.ssau.tk.enjoyers.ooplabs.models.Point;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;

    public FunctionService(FunctionRepository functionRepository, PointRepository pointRepository) {
        this.functionRepository = functionRepository;
        this.pointRepository = pointRepository;
    }

    @Transactional(readOnly = true)
    public Optional<Function> getFunction(Long id) {
        logger.debug("Getting function with id: {}", id);
        return functionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Function> getUserFunctions(Long userId) {
        logger.debug("Getting functions for user: {}", userId);
        return functionRepository.findByUserId(userId);
    }

    public void updateFunctionPoints(Long functionId, List<Point> newPoints) {
        logger.info("Updating points for function with id: {}", functionId);

        // Удаляем старые точки
        pointRepository.deleteByFunctionId(functionId);

        // Добавляем новые точки
        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found: " + functionId));

        newPoints.forEach(point -> point.setFunctionId(functionId));
        pointRepository.saveAll(newPoints);

        // Обновляем points_count
        function.setPointCount(newPoints.size());
        functionRepository.save(function);

        logger.info("Updated {} points for function with id: {}", newPoints.size(), functionId);
    }

    public Function createFunction(Function function) {
        logger.info("Creating function: {}", function.getName());

        // Сохраняем функцию
        Function savedFunction = functionRepository.save(function);

        logger.info("Created function with id: {}", savedFunction.getId());
        return savedFunction;
    }

    public void deleteFunction(Long functionId) {
        logger.info("Deleting function: {}", functionId);

        functionRepository.deleteById(functionId);

        logger.info("Deleted function: {}", functionId);
    }

    @Transactional(readOnly = true)
    public List<Point> getFunctionPoints(Long functionId) {
        logger.debug("Getting points for function: {}", functionId);
        return pointRepository.findByFunctionIdOrderByIndex(functionId);
    }

    @Transactional(readOnly = true)
    public long getUserFunctionCount(Long userId) {
        long count = functionRepository.countByUserId(userId);
        logger.debug("User {} has {} functions", userId, count);
        return count;
    }
}