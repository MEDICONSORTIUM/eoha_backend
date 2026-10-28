import path from "node:path";
import { fileURLToPath } from "node:url";

import express from "express";

import { corsMiddleware } from "./cors.js";
import { error } from "./lib/apiResponse.js";
import { BadParamError, ResourceNotFoundException } from "./lib/errors.js";
import healthRouter from "./routes/health.js";
import infoRouter from "./routes/info.js";
import malariaObservationsRouter from "./routes/malariaObservations.js";
import zonesRouter from "./routes/zones.js";

const publicDir = path.join(path.dirname(fileURLToPath(import.meta.url)), "..", "public");

/** Build the Express app (no `listen`) so it can be reused by tests. */
export function createApp() {
  const app = express();
  app.disable("x-powered-by");

  // CORS: what lets a browser UI on another origin call the API.
  app.use("/api", corsMiddleware);

  // REST endpoints.
  app.use("/api/health", healthRouter);
  app.use("/api/info", infoRouter);
  app.use("/api/v1/zones", zonesRouter);
  app.use("/api/v1/malaria-observations", malariaObservationsRouter);

  // Unmatched API route -> enveloped 404 (rather than the static handler / HTML).
  app.use("/api", (req, res) => {
    res.status(404).json(error("Not found"));
  });

  // Status dashboard served at `/` (index.html, app.js, styles.css).
  app.use(express.static(publicDir));

  // Turn exceptions into the standard ApiResponse error envelope.
  // eslint-disable-next-line no-unused-vars -- Express needs the 4-arg signature
  app.use((err, req, res, next) => {
    if (err instanceof ResourceNotFoundException) {
      return res.status(404).json(error(err.message));
    }
    if (err instanceof BadParamError) {
      return res.status(400).json(error(err.message));
    }
    console.error(err);
    return res.status(500).json(error("An unexpected error occurred"));
  });

  return app;
}
