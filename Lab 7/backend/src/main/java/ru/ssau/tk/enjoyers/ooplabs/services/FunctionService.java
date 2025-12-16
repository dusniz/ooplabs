package ru.ssau.tk.enjoyers.ooplabs.services;

import lombok.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.entities.Point;
import ru.ssau.tk.enjoyers.ooplabs.exceptions.TooComplex;
import ru.ssau.tk.enjoyers.ooplabs.functions.*;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionRepository;
import ru.ssau.tk.enjoyers.ooplabs.repositories.PointRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class FunctionService {

    private static final Logger logger = LogManager.getLogger(FunctionService.class);

    private final FunctionRepository functionRepository;
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public Optional<Function> getFunction(Long id) {
        logger.debug("Getting function with id: {}", id);
        return functionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Function> getFunctionByUserId(Long userId) {
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

        if (function.getPointCount() > 100000) {
            throw new TooComplex("Слишком много точек!");
        }

        Function savedFunction = functionRepository.save(function);

        logger.info("Created function with id: {}", savedFunction.getId());
        return savedFunction;
    }

    public Function updateFunction(Long functionId, Function function) {
        logger.info("Updating function: id = {}, name = {}", functionId, function.getName());
        Function updatedFunction;

        if (functionRepository.existsById(functionId)){
            function.setId(functionId);
            updatedFunction = functionRepository.save(function);
        }
        else {
            throw new IllegalArgumentException("No function with such id: " + functionId);
        }

        logger.info("Updated function with id: {}", functionId);
        return updatedFunction;
    }

    public void deleteFunction(Long functionId) {
        logger.info("Deleting function: {}", functionId);

        if (functionRepository.existsById(functionId)) {
            functionRepository.deleteById(functionId);
        }
        else {
            throw new IllegalArgumentException("No function with such id");
        }

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

    public double functionEvaluate(Function function, double x) {
        String functionClass = function.getFunctionClass();
        return switch (functionClass) {
            case "SqrFunction" -> {
                SqrFunction sqrFunction = new SqrFunction();
                yield sqrFunction.apply(x);
            }
            case "IdentityFunction" -> {
                IdentityFunction identityFunction = new IdentityFunction();
                yield identityFunction.apply(x);
            }
//            case "ConstantFunction" -> {
//                ConstantFunction constantFunction = new ConstantFunction();
//                yield constantFunction.apply(x);
//            }
            case "ZeroFunction" -> {
                ZeroFunction zeroFunction = new ZeroFunction();
                yield zeroFunction.apply(x);
            }
            case "UnitFunction" -> {
                UnitFunction unitFunction = new UnitFunction();
                yield unitFunction.apply(x);
            }
            case "NaturalLogarithmFunction" -> {
                NaturalLogarithmFunction naturalLogarithmFunction = new NaturalLogarithmFunction();
                yield naturalLogarithmFunction.apply(x);
            }
            case "SineFunction" -> {
                SineFunction sineFunction = new SineFunction();
                yield sineFunction.apply(x);
            }
            case "CosineFunction" -> {
                CosineFunction cosineFunction = new CosineFunction();
                yield cosineFunction.apply(x);
            }
            case "TangentFunction" -> {
                TangentFunction tangentFunction = new TangentFunction();
                yield tangentFunction.apply(x);
            }
            case "CotangentFunction" -> {
                CotangentFunction cotangentFunction = new CotangentFunction();
                yield cotangentFunction.apply(x);
            }
            default -> throw new IllegalArgumentException("No such function class found!");
        };
    }
}