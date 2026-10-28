/**
 * The standard response envelope. Every JSON endpoint returns this shape:
 * `{ success, data, message }` — matching the old Java `ApiResponse<T>`.
 */

export function success(data) {
  return { success: true, data, message: null };
}

export function error(message) {
  return { success: false, data: null, message };
}
