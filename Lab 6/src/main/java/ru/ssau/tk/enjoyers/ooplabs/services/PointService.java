package ru.ssau.tk.enjoyers.ooplabs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PointService {
    private static final Logger logger = LoggerFactory.getLogger(PointService.class);

    private final PointRepository pointRepository;
    private final FunctionRepository functionRepository;
    private final FunctionService functionService;

    public PointService(PointRepository pointRepository,
                        FunctionRepository functionRepository,
                        FunctionService functionService) {
        this.pointRepository = pointRepository;
        this.functionRepository = functionRepository;
        this.functionService = functionService;
    }

    @Transactional(readOnly = true)
    public List<Point> getPointsByFunctionId(Long functionId) {
        logger.debug("Getting points for function id: {}", functionId);
        validateFunctionExists(functionId);
        return pointRepository.findByFunctionIdOrderByIndex(functionId);
    }

    @Transactional(readOnly = true)
    public Optional<Point> getPointById(Long pointId) {
        logger.debug("Getting point by id: {}", pointId);
        return pointRepository.findById(pointId);
    }

    @Transactional(readOnly = true)
    public Optional<Point> getPointByFunctionAndIndex(Long functionId, Integer pointIndex) {
        logger.debug("Getting point at index {} for function id: {}", pointIndex, functionId);
        validateFunctionExists(functionId);
        return pointRepository.findByFunctionIdAndIndex(functionId, pointIndex);
    }

    public Point createPoint(Long functionId, Double x, Double y, Integer pointIndex) {
        logger.info("Creating point for function id: {}, index: {}, x: {}, y: {}",
                functionId, pointIndex, x, y);

        Function function = functionRepository.findById(functionId)
                .orElseThrow(() -> new IllegalArgumentException("Function not found with id: " + functionId));

        // Проверяем, не существует ли уже точки с таким индексом
        if (pointRepository.existsByFunctionIdAndIndex(functionId, pointIndex)) {
            throw new IllegalArgumentException("Point with index " + pointIndex + " already exists for function " + functionId);
        }

        Point point = new Point(functionId, x, y, pointIndex);
        Point savedPoint = pointRepository.save(point);

        // Обновляем points_count функции
        updateFunctionPointsCount(functionId);

        logger.info("Created point with id: {}", savedPoint.getId());
        return savedPoint;
    }

    public Point updatePoint(Long pointId, Double newX, Double newY) {
        logger.info("Updating point id: {}, newX: {}, newY: {}", pointId, newX, newY);

        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found with id: " + pointId));

        point.setX(newX);
        point.setY(newY);

        Point updatedPoint = pointRepository.save(point);
        logger.info("Updated point id: {}", pointId);
        return updatedPoint;
    }

    public Point updatePointByIndex(Long functionId, Integer pointIndex, Double newX, Double newY) {
        logger.info("Updating point at index {} for function id: {}, newX: {}, newY: {}",
                pointIndex, functionId, newX, newY);

        Point point = pointRepository.findByFunctionIdAndIndex(functionId, pointIndex)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Point not found at index " + pointIndex + " for function " + functionId));

        point.setX(newX);
        point.setY(newY);

        Point updatedPoint = pointRepository.save(point);
        logger.info("Updated point at index {} for function id: {}", pointIndex, functionId);
        return updatedPoint;
    }

    public void deletePoint(Long pointId) {
        logger.info("Deleting point id: {}", pointId);

        Point point = pointRepository.findById(pointId)
                .orElseThrow(() -> new IllegalArgumentException("Point not found with id: " + pointId));

        Long functionId = point.getFunctionId();
        pointRepository.delete(point);

        // Обновляем points_count и переиндексируем оставшиеся точки
        updateFunctionPointsCount(functionId);
        reindexPoints(functionId);

        logger.info("Deleted point id: {}", pointId);
    }

    public void deletePointByIndex(Long functionId, Integer pointIndex) {
        logger.info("Deleting point at index {} for function id: {}", pointIndex, functionId);

        Point point = pointRepository.findByFunctionIdAndIndex(functionId, pointIndex)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Point not found at index " + pointIndex + " for function " + functionId));

        pointRepository.delete(point);

        // Обновляем points_count и переиндексируем оставшиеся точки
        updateFunctionPointsCount(functionId);
        reindexPoints(functionId);

        logger.info("Deleted point at index {} for function id: {}", pointIndex, functionId);
    }

    public void deleteAllPointsByFunctionId(Long functionId) {
        logger.info("Deleting all points for function id: {}", functionId);
        validateFunctionExists(functionId);

        pointRepository.deleteByFunctionId(functionId);
        updateFunctionPointsCount(functionId);

        logger.info("Deleted all points for function id: {}", functionId);
    }

    private void validateFunctionExists(Long functionId) {
        if (!functionRepository.existsById(functionId)) {
            throw new IllegalArgumentException("Function not found with id: " + functionId);
        }
    }

    private void updateFunctionPointsCount(Long functionId) {
        long pointsCount = pointRepository.countByFunctionId(functionId);
        functionRepository.findById(functionId).ifPresent(function -> {
            function.setPointCount((int) pointsCount);
            functionRepository.save(function);
        });
    }

    public void reindexPoints(Long functionId) {
        logger.info("Reindexing points for function id: {}", functionId);

        List<Point> points = pointRepository.findByFunctionIdOrderByIndex(functionId);
        for (int i = 0; i < points.size(); i++) {
            points.get(i).setIndex(i);
        }
        pointRepository.saveAll(points);

        logger.info("Reindexed {} points for function id: {}", points.size(), functionId);
    }
}