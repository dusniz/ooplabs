package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.dto.FunctionDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.PointDto;
import ru.ssau.tk.enjoyers.ooplabs.dto.SearchCriteria;
import ru.ssau.tk.enjoyers.ooplabs.dto.SearchResult;
import ru.ssau.tk.enjoyers.ooplabs.DatabaseConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdvancedJdbcFunctionDao implements SearchableDao<FunctionDto> {
    private static final Logger logger = LogManager.getLogger(AdvancedJdbcFunctionDao.class);

    private final JdbcFunctionDao baseDao;

    public AdvancedJdbcFunctionDao(JdbcFunctionDao baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public SearchResult<FunctionDto> search(SearchCriteria criteria) {
        List<FunctionDto> results = new ArrayList<>();
        int totalCount = 0;

        StringBuilder sql = new StringBuilder("SELECT * FROM functions WHERE 1=1");
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM functions WHERE 1=1");

        List<Object> parameters = new ArrayList<>();
        List<Object> countParameters = new ArrayList<>();

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

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get total count
            try (PreparedStatement countStmt = conn.prepareStatement(countSql.toString())) {
                setParameters(countStmt, countParameters);
                ResultSet countRs = countStmt.executeQuery();
                if (countRs.next()) {
                    totalCount = countRs.getInt(1);
                }
            }

            // Get paginated results
            try (PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
                setParameters(stmt, parameters);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    FunctionDto function = mapResultSetToFunction(rs);
                    results.add(function);
                }
            }

            logger.debug("Advanced search found {} functions", results.size());

        } catch (SQLException e) {
            logger.error("Error in advanced search: {}", e.getMessage());
        }

        int page = criteria.getOffset() != null ? criteria.getOffset() / (criteria.getLimit() != null ? criteria.getLimit() : 1) + 1 : 1;
        int pageSize = criteria.getLimit() != null ? criteria.getLimit() : results.size();

        return new SearchResult<>(results, totalCount, page, pageSize);
    }

    @Override
    public List<FunctionDto> findAll() {
        return baseDao.findByUserId(1L); // В реальности нужен другой подход
    }

    @Override
    public List<FunctionDto> findByField(String fieldName, Object value) {
        SearchCriteria criteria = new SearchCriteria()
                .addCondition(fieldName, SearchCriteria.Operator.EQUALS, value);
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findByFieldLike(String fieldName, String pattern) {
        SearchCriteria criteria = new SearchCriteria()
                .addCondition(fieldName, SearchCriteria.Operator.LIKE, "%" + pattern + "%");
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findByFieldIn(String fieldName, List<?> values) {
        SearchCriteria criteria = new SearchCriteria()
                .addCondition(fieldName, SearchCriteria.Operator.IN, values);
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findWithPagination(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        SearchCriteria criteria = new SearchCriteria()
                .paginate(pageSize, offset);
        return search(criteria).getItems();
    }

    @Override
    public List<FunctionDto> findWithSorting(String sortField, SearchCriteria.SortDirection direction) {
        SearchCriteria criteria = new SearchCriteria()
                .sortBy(sortField, direction);
        return search(criteria).getItems();
    }

    // Поиск в глубину - рекурсивный поиск по связанным данным
    public List<FunctionDto> depthFirstSearch(Long startUserId, String namePattern) {
        List<FunctionDto> results = new ArrayList<>();
        depthFirstSearchRecursive(startUserId, namePattern, results, new ArrayList<>());
        return results;
    }

    private void depthFirstSearchRecursive(Long userId, String namePattern,
                                           List<FunctionDto> results, List<Long> visited) {
        if (visited.contains(userId)) return;
        visited.add(userId);

        // Ищем функции пользователя
        List<FunctionDto> userFunctions = findByFieldLike("name", namePattern);
        results.addAll(userFunctions);

        // В реальном проекте здесь был бы рекурсивный поиск по связанным пользователям
        // Например, если бы была иерархия пользователей (менеджер -> сотрудники)
    }

    // Поиск в ширину
    public List<FunctionDto> breadthFirstSearch(Long startUserId, String namePattern) {
        List<FunctionDto> results = new ArrayList<>();
        List<Long> queue = new ArrayList<>();
        List<Long> visited = new ArrayList<>();

        queue.add(startUserId);

        while (!queue.isEmpty()) {
            Long currentUserId = queue.remove(0);

            if (visited.contains(currentUserId)) continue;
            visited.add(currentUserId);

            // Ищем функции пользователя
            List<FunctionDto> userFunctions = baseDao.findByUserId(currentUserId).stream()
                    .filter(f -> f.getName().contains(namePattern))
                    .toList();
            results.addAll(userFunctions);

            // В реальном проекте добавляем связанных пользователей в очередь
            // queue.addAll(getRelatedUserIds(currentUserId));
        }

        return results;
    }

    // Множественный поиск с разными критериями
    public SearchResult<FunctionDto> multiFieldSearch(List<SearchCriteria> criteriaList) {
        if (criteriaList == null || criteriaList.isEmpty()) {
            return new SearchResult<>(new ArrayList<>(), 0, 1, 10);
        }

        // Объединяем условия через OR
        List<FunctionDto> allResults = new ArrayList<>();
        int totalCount = 0;

        for (SearchCriteria criteria : criteriaList) {
            SearchResult<FunctionDto> result = search(criteria);
            allResults.addAll(result.getItems());
            totalCount += result.getTotalCount();
        }

        // Убираем дубликаты
        List<FunctionDto> uniqueResults = allResults.stream()
                .distinct()
                .toList();

        return new SearchResult<>(uniqueResults, uniqueResults.size(), 1, uniqueResults.size());
    }

    // Вспомогательные методы
    private void buildWhereClause(SearchCriteria criteria, StringBuilder sql, StringBuilder countSql,
                                  List<Object> parameters, List<Object> countParameters) {
        for (SearchCriteria.Condition condition : criteria.getConditions()) {
            String whereClause = buildConditionClause(condition, parameters, countParameters);
            sql.append(" AND ").append(whereClause);
            countSql.append(" AND ").append(whereClause);
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
                return field + " = ?";
        }
    }

    private String buildInClause(String field, List<?> values,
                                 List<Object> parameters, List<Object> countParameters) {
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
        for (int i = 0; i < parameters.size(); i++) {
            stmt.setObject(i + 1, parameters.get(i));
        }
    }

    private FunctionDto mapResultSetToFunction(ResultSet rs) throws SQLException {
        return new FunctionDto(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("name"),
                rs.getString("type"),
                rs.getString("description"),
                rs.getInt("points_count"),
                rs.getString("function_class")
        );
    }
}
