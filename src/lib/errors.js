/**
 * Thrown by the service layer when a requested entity does not exist. Mapped to
 * HTTP 404 by the error handler in `app.js`. Anything else is treated as an
 * unexpected server error (HTTP 500).
 */
export class ResourceNotFoundException extends Error {
  constructor(message) {
    super(message);
    this.name = "ResourceNotFoundException";
  }
}

/**
 * Thrown when a path/query parameter can't be coerced to the expected type
 * (e.g. `/api/v1/zones/abc`, `?date=nonsense`). Mapped to HTTP 400. Equivalent to
 * Spring's `MethodArgumentTypeMismatchException`.
 */
export class BadParamError extends Error {
  constructor(paramName) {
    super(`Invalid value for parameter '${paramName}'`);
    this.name = "BadParamError";
    this.paramName = paramName;
  }
}
