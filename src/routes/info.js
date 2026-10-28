import { Router } from "express";

import { config } from "../config.js";
import { success } from "../lib/apiResponse.js";

const router = Router();

/** Runtime metadata consumed by the status dashboard served at `/`. */
router.get("/", (req, res) => {
  res.json(
    success({
      name: config.name,
      version: config.version,
      apiVersion: config.apiVersion,
      startedAt: config.startedAt.toISOString(),
      corsAllowedOrigins: config.corsAllowedOrigins,
    }),
  );
});

export default router;
