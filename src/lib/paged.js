/**
 * Pagination envelope for list endpoints. Slices an in-memory array — there is no
 * database behind this placeholder API. Ported 1:1 from `PagedResponse.of(...)`.
 */

export function paginate(all, page, size) {
  if (page < 0) page = 0;
  if (size < 1) size = 20;

  const total = all.length;
  const totalPages = Math.ceil(total / size);
  const from = Math.min(page * size, total);
  const to = Math.min(from + size, total);

  return {
    content: all.slice(from, to),
    page,
    size,
    totalElements: total,
    totalPages,
    first: page === 0,
    last: page >= totalPages - 1,
  };
}
