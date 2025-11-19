package ru.ssau.tk.enjoyers.ooplabs.dao;

import ru.ssau.tk.enjoyers.ooplabs.entity.SearchCriteria;
import ru.ssau.tk.enjoyers.ooplabs.entity.SearchResult;
import java.util.List;

public interface SearchableDao<T> {

    SearchResult<T> search(SearchCriteria criteria);

    List<T> findAll();

    List<T> findByField(String fieldName, Object value);

    List<T> findByFieldLike(String fieldName, String pattern);

    List<T> findByFieldIn(String fieldName, List<?> values);

    List<T> findWithPagination(int page, int pageSize);

    List<T> findWithSorting(String sortField, SearchCriteria.SortDirection direction);
}
