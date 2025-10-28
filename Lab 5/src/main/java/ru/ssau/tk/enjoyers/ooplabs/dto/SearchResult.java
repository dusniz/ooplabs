package ru.ssau.tk.enjoyers.ooplabs.dto;

import java.util.List;

public class SearchResult<T> {
    private final List<T> items;
    private final int totalCount;
    private final int page;
    private final int pageSize;

    public SearchResult(List<T> items, int totalCount, int page, int pageSize) {
        this.items = items;
        this.totalCount = totalCount;
        this.page = page;
        this.pageSize = pageSize;
    }

    public List<T> getItems() { return items; }
    public int getTotalCount() { return totalCount; }
    public int getPage() { return page; }
    public int getPageSize() { return pageSize; }
    public int getTotalPages() {
        return (int) Math.ceil((double) totalCount / pageSize);
    }

    public boolean hasNext() {
        return page * pageSize < totalCount;
    }

    public boolean hasPrevious() {
        return page > 1;
    }
}
