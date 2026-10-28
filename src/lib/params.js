import { BadParamError } from "./errors.js";

/**
 * Coerce request parameters the way Spring's `@RequestParam` / `@PathVariable`
 * binding did: a present-but-unparseable value is a 400 (`BadParamError`), a
 * missing optional value falls back to the default.
 */

/** Required integer (path variable). Throws `BadParamError` if not a whole number. */
export function requireInt(value, paramName) {
  if (typeof value !== "string" || !/^[+-]?\d+$/.test(value)) {
    throw new BadParamError(paramName);
  }
  const parsed = Number(value);
  if (!Number.isSafeInteger(parsed) || parsed < -2147483648 || parsed > 2147483647) {
    throw new BadParamError(paramName);
  }
  return parsed;
}

/** Optional integer query param. `undefined` -> `fallback`; present-but-bad -> 400. */
export function optionalInt(value, paramName, fallback) {
  if (value === undefined || value === "") return fallback;
  return requireInt(value, paramName);
}

/** Optional ISO date (`YYYY-MM-DD`) query param. `undefined` -> `null`; bad -> 400. */
export function optionalIsoDate(value, paramName) {
  if (value === undefined) return null;
  if (typeof value !== "string" || !/^\d{4}-\d{2}-\d{2}$/.test(value)) {
    throw new BadParamError(paramName);
  }
  const [y, m, d] = value.split("-").map(Number);
  const dt = new Date(Date.UTC(y, m - 1, d));
  const valid =
    dt.getUTCFullYear() === y && dt.getUTCMonth() === m - 1 && dt.getUTCDate() === d;
  if (!valid) throw new BadParamError(paramName);
  return value;
}

/** Optional string query param. `undefined` -> `null`; otherwise passed through
 *  verbatim (an empty `?province=` filters to nothing, matching the old binding). */
export function optionalString(value) {
  return value === undefined ? null : String(value);
}
