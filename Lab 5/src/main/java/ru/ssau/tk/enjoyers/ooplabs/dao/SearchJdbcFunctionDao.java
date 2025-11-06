package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.SearchCriteria;
import ru.ssau.tk.enjoyers.ooplabs.dto.SearchResult;
import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchJdbcFunctionDao implements SearchableDao<FunctionDto> {
    private static final Logger logger = LogManager.getLogger(SearchJdbcFunctionDao.class);

    private final JdbcFunctionDao baseDao;

    public SearchJdbcFunctionDao(JdbcFunctionDao baseDao) {
        this.baseDao = baseDao;
        logger.info("SearchJdbcFunctionDao инициализирован");
    }

    @Override
    public SearchResult<FunctionDto> search(SearchCriteria criteria) {
        logger.debug("Начало поиска с критериями: {}", criteria);

        if (logger.isDebugEnabled()) {
            logger.debug("Детали критериев поиска: limit={}, offset={}, sortField={}, conditions={}",
                    criteria.getLimit(), criteria.getOffset(),
                    criteria.getSortField(), criteria.getConditions().size());
        }

        List<FunctionDto> results = new ArrayList<>();
        int totalCount = 0;
        long startTime = System.currentTimeMillis();

        StringBuilder sql = new StringBuilder("SELECT * FROM functions WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM functions WHERE 1=1");

        List<Object> parameters = new ArrayList<>();
        List<Object> countParameters = new ArrayList<>();

        try {
            // Build WHERE conditions
            buildWhereClause(criteria, sql, countSql, parameters, countParameters);

            // Build ORDER BY
            if (criteria.getSortField() != null) {
                sql.append(" ORDER BY ").append(criteria.getSortField())
                        .append(" ").append(criteria.getSortDirection());
            }

            // Build LIMIT and OFFSET
            if (criteria.getLimit() != null) {
                sql.append(" LIMIT ?");
                parameters.add(criteria.getLimit());
            }
            if (criteria.getOffset() != null) {
                sql.append(" OFFSET ?");
                parameters.add(criteria.getOffset());
            }

            logger.debug("Сформирован SQL запрос: {}", sql.toString());
            logger.debug("Параметры запроса: {}", parameters);

            try (Connection conn = DatabaseConnection.getConnection()) {
                logger.trace("Выполнение count запроса: {}", countSql.toString());
                try (PreparedStatement countStmt = conn.prepareStatement(countSql.toString())) {
                    setParameters(countStmt, countParameters);
                    ResultSet countRs = countStmt.executeQuery();
                    if (countRs.next()) {
                        totalCount = countRs.getInt(1);
                    }
                }

                logger.trace("Выполнение основного запроса с пагинацией");
                try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                    setParameters(stmt, parameters);
                    ResultSet rs = stmt.executeQuery();

                    while (rs.next()) {
                        FunctionDto function = mapResultSetToFunction(rs);
                        results.add(function);
                    }
                }

                long duration = System.currentTimeMillis() - startTime;
                logger.info("Поиск завершен: найдено {} функций из {}, время выполнения: {} мс",
                        results.size(), totalCount, duration);

                if (logger.isDebugEnabled()) {
                    logger.debug("ID найденных функций: {}",
                            results.stream().map(FunctionDto::getId).toList());
                }

                logger.debug("Поиск нашёл {} функций", results.size());

            } catch (SQLException e) {
                logger.error("Ошибка при выполнении поиска: {}", e.getMessage());
            }
        } catch(Exception e) {
            logger.error("Непредвиденная ошибка при поиске: {}", e.getMessage(), e);
        }

        int page = criteria.getOffset() != null ? criteria.getOffset() / (criteria.getLimit() != null ? criteria.getLimit() : 1) + 1 : 1;
        int pageSize = criteria.getLimit() != null ? criteria.getLimit() : results.size();

        logger.debug("Формирование результата поиска: страница {}, размер страницы {}", page, pageSize);

        return new SearchResult<>(results, totalCount, page, pageSize);
    }

    @Override
    public List<FunctionDto> findAll() {
        return baseDao.findByUserId(1L);
    }

    @Override
    public List<FunctionDto> findByField(String fieldName, Object value) {
        logger.debug("Поиск по полю: {} = {}", fieldName, value);
        SearchCriteria criteria = new SearchCriteria()
                .addCondition(fieldName, SearchCriteria.Operator.EQUALS, value);
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findByFieldLike(String fieldName, String pattern) {
        logger.debug("Поиск по шаблону: {} LIKE {}", fieldName, pattern);
        SearchCriteria criteria = new SearchCriteria()
                .addCondition(fieldName, SearchCriteria.Operator.LIKE, "%" + pattern + "%");
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findByFieldIn(String fieldName, List<?> values) {
        logger.debug("Поиск по списку значений: {} IN ({} значений)", fieldName, values.size());
        if (logger.isTraceEnabled()) {
            logger.trace("Значения для поиска IN: {}", values);
        }
        SearchCriteria criteria = new SearchCriteria()
                .addCondition(fieldName, SearchCriteria.Operator.IN, values);
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findWithPagination(int page, int pageSize) {
        logger.debug("Поиск с пагинацией: страница {}, размер {}", page, pageSize);
        int offset = (page - 1) * pageSize;
        SearchCriteria criteria = new SearchCriteria()
                .paginate(pageSize, offset);
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findWithSorting(String sortField, SearchCriteria.SortDirection direction) {
        logger.debug("Поиск с сортировкой: поле {}, направление {}", sortField, direction);
        SearchCriteria criteria = new SearchCriteria()
                .sortBy(sortField, direction);
        return search(criteria).getItems();
    }

    // Поиск в глубину - рекурсивный поиск по связанным данным
    public List<FunctionDto> depthFirstSearch(Long startUserId, String namePattern) {
        logger.info("Запуск поиска в глубину: userId={}, pattern='{}'", startUserId, namePattern);
        List<FunctionDto> results = new ArrayList<>();
        depthFirstSearchRecursive(startUserId, namePattern, results, new ArrayList<>());
        logger.info("Поиск в глубину завершен: найдено {} результатов", results.size());
        return results;
    }

    private void depthFirstSearchRecursive(Long userId, String namePattern,
                                           List<FunctionDto> results, List<Long> visited) {

        logger.trace("Рекурсивный поиск: userId={}, посещено {}", userId, visited.size());
        if (visited.contains(userId)) {
            logger.trace("Пользователь {} уже посещен, пропускаем", userId);
            return;
        }
        visited.add(userId);

        // Ищем функции пользователя
        List<FunctionDto> userFunctions = findByFieldLike("name", namePattern);
        results.addAll(userFunctions);

        logger.trace("На уровне userId={} найдено {} функций", userId, userFunctions.size());
    }

    // Поиск в ширину
    public List<FunctionDto> breadthFirstSearch(Long startUserId, String namePattern) {
        logger.info("Запуск поиска в ширину: userId={}, pattern='{}'", startUserId, namePattern);
        List<FunctionDto> results = new ArrayList<>();
        List<Long> queue = new ArrayList<>();
        List<Long> visited = new ArrayList<>();

        queue.add(startUserId);
        int level = 0;

        while (!queue.isEmpty()) {
            level++;
            logger.debug("Уровень {} поиска в ширину: в очереди {} элементов", level, queue.size());

            Long currentUserId = queue.remove(0);

            if (visited.contains(currentUserId)) {
                logger.trace("Пользователь {} уже посещен, пропускаем", currentUserId);
                continue;
            }
            visited.add(currentUserId);

            List<FunctionDto> userFunctions = baseDao.findByUserId(currentUserId).stream()
                    .filter(f -> f.getName().contains(namePattern))
                    .toList();
            results.addAll(userFunctions);

            logger.trace("На уровне {} для userId={} найдено {} функций", level, currentUserId, userFunctions.size());
        }

        return results;
    }

    // Множественный поиск с разными критериями
    public SearchResult<FunctionDto> multiFieldSearch(List<SearchCriteria> criteriaList) {
        logger.info("Множественный поиск по {} критериям", criteriaList != null ? criteriaList.size() : 0);
        if (criteriaList == null || criteriaList.isEmpty()) {
            logger.warn("Передан пустой список критериев для множественного поиска");
            return new SearchResult<>(new ArrayList<>(), 0, 1, 10);
        }

        if (logger.isDebugEnabled()) {
            for (int i = 0; i < criteriaList.size(); i++) {
                logger.debug("Критерий {}: {}", i, criteriaList.get(i));
            }
        }

        // Объединяем условия через OR
        List<FunctionDto> allResults = new ArrayList<>();
        int totalCount = 0;
        long startTime = System.currentTimeMillis();

        for (SearchCriteria criteria : criteriaList) {
            SearchResult<FunctionDto> result = search(criteria);
            allResults.addAll(result.getItems());
            totalCount += result.getTotalCount();
        }

        // Убираем дубликаты
        List<FunctionDto> uniqueResults = allResults.stream()
                .distinct()
                .toList();

        long duration = System.currentTimeMillis() - startTime;
        logger.info("Множественный поиск завершен: {} уникальных результатов из {}, время: {} мс",
                uniqueResults.size(), totalCount, duration);

        return new SearchResult<>(uniqueResults, uniqueResults.size(), 1, uniqueResults.size());
    }

    // Вспомогательные методы
    private void buildWhereClause(SearchCriteria criteria, StringBuilder sql, StringBuilder countSql,
                                  List<Object> parameters, List<Object> countParameters) {
        logger.trace("Построение WHERE clause для {} условий", criteria.getConditions().size());
        for (SearchCriteria.Condition condition : criteria.getConditions()) {
            String whereClause = buildConditionClause(condition, parameters, countParameters);
            sql.append(" AND ").append(whereClause);
            countSql.append(" AND ").append(whereClause);

            logger.trace("Добавлено условие: {}", whereClause);
        }
    }

    private String buildConditionClause(SearchCriteria.Condition condition,
                                        List<Object> parameters, List<Object> countParameters) {
        String field = condition.getField();
        SearchCriteria.Operator operator = condition.getOperator();
        Object value = condition.getValue();

        parameters.add(value);
        countParameters.add(value);

        switch (operator) {
            case EQUALS:
                return field + " = ?";
            case NOT_EQUALS:
                return field + " != ?";
            case GREATER_THAN:
                return field + " > ?";
            case LESS_THAN:
                return field + " < ?";
            case GREATER_OR_EQUAL:
                return field + " >= ?";
            case LESS_OR_EQUAL:
                return field + " <= ?";
            case LIKE:
                parameters.set(parameters.size() - 1, "%" + value + "%");
                countParameters.set(countParameters.size() - 1, "%" + value + "%");
                return field + " LIKE ?";
            case IN:
                // Специальная обработка для IN
                return buildInClause(field, (List<?>) value, parameters, countParameters);
            default:
                logger.warn("Неизвестный оператор {}, использован оператор по умолчанию", operator);
                return field + " = ?";
        }
    }

    private String buildInClause(String field, List<?> values,
                                 List<Object> parameters, List<Object> countParameters) {

        logger.trace("Построение IN clause для {} значений", values.size());
        parameters.remove(parameters.size() - 1); // Удаляем добавленный ранее параметр
        countParameters.remove(countParameters.size() - 1);

        StringBuilder inClause = new StringBuilder(field + " IN (");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) inClause.append(", ");
            inClause.append("?");
            parameters.add(values.get(i));
            countParameters.add(values.get(i));
        }
        inClause.append(")");

        return inClause.toString();
    }

    private void setParameters(PreparedStatement stmt, List<Object> parameters) throws SQLException {
        if (logger.isTraceEnabled()) {
            logger.trace("Установка {} параметров в PreparedStatement", parameters.size());
        }
        for (int i = 0; i < parameters.size(); i++) {
            stmt.setObject(i + 1, parameters.get(i));

            if (logger.isTraceEnabled()) {
                logger.trace("Параметр {}: {}", i + 1, parameters.get(i));
            }
        }
    }

    private FunctionDto mapResultSetToFunction(ResultSet rs) throws SQLException {
        FunctionDto function = new FunctionDto(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getInt("point_count"),
                rs.getString("function_class")
        );
        logger.trace("Сопоставлен ResultSet с FunctionDto: {}", function);
        return function;
    }
}
