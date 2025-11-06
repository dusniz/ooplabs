package ru.ssau.tk.enjoyers.ooplabs.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.criteria.Predicate;
import ru.ssau.tk.enjoyers.ooplabs.entities.Function;
import ru.ssau.tk.enjoyers.ooplabs.repositories.FunctionSearchRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.LinkedList;

@Service
public class AdvancedSearchService {
    private static final Logger logger = LoggerFactory.getLogger(AdvancedSearchService.class);

    @Autowired
    private FunctionSearchRepository functionSearchRepository;

    // Поиск в глубину (Depth-First Search)
    public List<Function> depthFirstSearch(Long startUserId, String searchTerm) {
        logger.info("Called DFS for user {}", startUserId);
        List<Function> results = new ArrayList<>();
        List<Long> visited = new ArrayList<>();

        depthFirstSearchRecursive(startUserId, searchTerm, results, visited);
        logger.info("DFS found {} functions for user {}", results.size(), startUserId);
        return results;
    }

    private void depthFirstSearchRecursive(Long userId, String searchTerm,
                                           List<Function> results, List<Long> visited) {
        if (visited.contains(userId)) return;
        visited.add(userId);

        // Ищем функции текущего пользователя
        List<Function> userFunctions = functionSearchRepository.findByNameContaining(searchTerm).stream()
                .filter(f -> f.getUserId().equals(userId))
                .toList();
        results.addAll(userFunctions);
    }

    // Поиск в ширину (Breadth-First Search)
    public List<Function> breadthFirstSearch(Long startUserId, String searchTerm) {
        logger.info("Called BFS for user {}", startUserId);

        List<Function> results = new ArrayList<>();
        Queue<Long> queue = new LinkedList<>();
        List<Long> visited = new ArrayList<>();

        queue.offer(startUserId);

        while (!queue.isEmpty()) {
            Long currentUserId = queue.poll();

            if (visited.contains(currentUserId)) continue;
            visited.add(currentUserId);

            // Ищем функции текущего пользователя
            List<Function> userFunctions = functionSearchRepository.findByNameContaining(searchTerm).stream()
                    .filter(f -> f.getUserId().equals(currentUserId))
                    .toList();
            results.addAll(userFunctions);
        }

        logger.info("BFS found {} functions for user {}", results.size(), startUserId);
        return results;
    }

    // Динамический поиск с Specification
    public List<Function> dynamicSearch(Long userId, String name, String type,
                                              Integer minPoints, Integer maxPoints) {
        logger.info("Called DynamicSearch for user {}", userId);

        Specification<Function> spec = buildSearchSpecification(userId, name, type, minPoints, maxPoints);
        List<Function> results = functionSearchRepository.findAll(spec);

        logger.info("DynamicSearch found {} functions for user {}", results.size(), userId);
        return results;
    }

    public Page<Function> dynamicSearchWithPagination(Long userId, String name, String type,
                                                            Integer minPoints, Integer maxPoints,
                                                            int page, int size, String sortBy, String sortDir) {
        logger.info("Called DynamicSearch with pagination for user {}", userId);

        Specification<Function> spec = buildSearchSpecification(userId, name, type, minPoints, maxPoints);

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Function> results = functionSearchRepository.findAll(spec, pageable);

        logger.info("DynamicSearch with pagination found {} functions for user {}", results.stream().toList().size(), userId);
        return results;
    }

    private Specification<Function> buildSearchSpecification(Long userId, String name,
                                                                   String type, Integer minPoints, Integer maxPoints) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (userId != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), userId));
            }
            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }
            if (type != null && !type.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("type"), type));
            }
            if (minPoints != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("pointsCount"), minPoints));
            }
            if (maxPoints != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("pointsCount"), maxPoints));
            }

            logger.info("Built SearchSpecification for user {}", userId);
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    // Множественный поиск с разными стратегиями
    public List<Function> multiStrategySearch(Long userId, String searchTerm, String strategy) {
        logger.info("Called MultiStrategySearch function for user {}", userId);
        return switch (strategy.toLowerCase()) {
            case "dfs" -> depthFirstSearch(userId, searchTerm);
            case "bfs" -> breadthFirstSearch(userId, searchTerm);
            case "simple" -> functionSearchRepository.findByNameContaining(searchTerm).stream()
                    .filter(f -> f.getUserId().equals(userId))
                    .toList();
            case "advanced" -> dynamicSearch(userId, searchTerm, null, null, null);
            default -> throw new IllegalArgumentException("Unknown search strategy: " + strategy);
        };
    }

    // Поиск с сортировкой по разным полям
    public List<Function> searchWithSorting(Long userId, String sortField, boolean ascending) {
        logger.info("Called SearchWithSorting function for user {}", userId);
        return switch (sortField.toLowerCase()) {
            case "name" -> ascending ?
                    functionSearchRepository.findByUserIdOrderByNameAsc(userId) :
                    functionSearchRepository.findByUserId(userId,
                            PageRequest.of(0, Integer.MAX_VALUE, Sort.by("name").descending())).getContent();
            case "points" -> ascending ?
                    functionSearchRepository.findByUserId(userId,
                            PageRequest.of(0, Integer.MAX_VALUE, Sort.by("pointsCount").ascending())).getContent() :
                    functionSearchRepository.findByUserIdOrderByPointsCountDesc(userId);
            case "date" -> ascending ?
                    functionSearchRepository.findByUserId(userId,
                            PageRequest.of(0, Integer.MAX_VALUE, Sort.by("createdAt").ascending())).getContent() :
                    functionSearchRepository.findByUserIdOrderByCreatedAtDesc(userId);
            default -> functionSearchRepository.findByUserId(userId,
                    PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        };
    }
}