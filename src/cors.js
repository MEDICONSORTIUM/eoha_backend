import cors from "cors";

import { config } from "./config.js";

/**
 * CORS is what lets a browser UI served from another origin call this API.
 *
 * The allowed origins are *patterns*: a value like `http://localhost:[*]` (or
 * `http://localhost:*`) matches any local dev port (Live Server 5500, Vite 5173,
 * CRA 3000, `python -m http.server`, ...). Set `CORS_ALLOWED_ORIGINS` to the real
 * site origin(s) in production. A page opened as a `file://` URL sends
 * `Origin: null` and will not match — serve the UI over http.
 */

function patternToRegExp(pattern) {
  const escaped = pattern.replace(/[.*+?^${}()|[\]\\]/g, "\\$&");
  const withWildcards = escaped
    // `[*]` in the port position -> one or more digits
    .replace(/:\\\[\\\*\\\]/g, ":\\d+")
    .replace(/:\\\*/g, ":\\d+")
    // any remaining `*` -> "anything but a slash"
    .replace(/\\\*/g, "[^/]*");
  return new RegExp(`^${withWildcards}$`);
}

const originMatchers = config.corsAllowedOrigins.map(patternToRegExp);

export const corsOptions = {
  origin(origin, callback) {
    // No Origin header (curl, same-origin navigation) — allow, no CORS headers needed.
    if (!origin) return callback(null, true);
    const allowed = originMatchers.some((re) => re.test(origin));
    callback(null, allowed);
  },
  methods: ["GET", "HEAD", "OPTIONS"],
  // Reflect requested headers, as Spring does for allowedHeaders("*").
  maxAge: 3600,
};

export const corsMiddleware = cors(corsOptions);
