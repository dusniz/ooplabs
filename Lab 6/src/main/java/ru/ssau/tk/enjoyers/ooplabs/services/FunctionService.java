package ru.ssau.tk.enjoyers.ooplabs.services;

import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.mappers.FunctionMapper;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class FunctionService {
    private static final Logger logger = LoggerFactory.getLogger(FunctionService.class);

    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;
    private final FunctionMapper functionMapper;

    @Transactional(readOnly = true)
    public Optional<FunctionDto> getFunctionDto(Long id) {
        logger.debug("Getting function DTO with id: {}", id);
        return functionRepository.findById(id)
                .map(functionMapper::FunctiontoFunctionDto);
    }

    @Transactional(readOnly = true)
    public List<FunctionDto> getUserFunctionDtos(Long userId) {
        logger.debug("Getting function DTOs for user: {}", userId);
        return functionRepository.findByUserId(userId).stream()
                .map(functionMapper::FunctiontoFunctionDto)
                .collect(Collectors.toList());
    }

    public FunctionDto createFunctionFromDto(FunctionDto functionDto) {
        logger.info("Creating function from DTO: {}", functionDto.getName());

        Function function = functionMapper.FunctionDtotoFunction(functionDto);
        Function savedFunction = functionRepository.save(function);

        logger.info("Created function from DTO with id: {}", savedFunction.getId());
        return functionMapper.FunctiontoFunctionDto(savedFunction);
    }

    public Optional<FunctionDto> updateFunctionFromDto(Long id, FunctionDto functionDto) {
        logger.info("Updating function from DTO with id: {}", id);

        return functionRepository.findById(id)
                .map(existingFunction -> {
                    // Обновляем поля с помощью MapStruct
                    functionMapper.updateEntityFromDto(functionDto, existingFunction);
                    Function updatedFunction = functionRepository.save(existingFunction);
                    return functionMapper.FunctiontoFunctionDto(updatedFunction);
                });
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