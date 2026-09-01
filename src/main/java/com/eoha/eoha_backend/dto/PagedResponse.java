package com.eoha.eoha_backend.dto;

import java.util.List;

/**
 * Pagination envelope for list endpoints. Slices an in-memory list — there is no
 * database behind this placeholder API.
 */
public record PagedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {

    public static <T> PagedResponse<T> of(List<T> all, int page, int size) {
        if (page < 0) page = 0;
        if (size < 1) size = 20;

        int total = all.size();
        int totalPages = (int) Math.ceil(total / (double) size);
        int from = Math.min(page * size, total);
        int to = Math.min(from + size, total);

        return new PagedResponse<>(
                all.subList(from, to),
                page,
                size,
                total,
                totalPages,
                page == 0,
                page >= totalPages - 1
        );
    }
}
