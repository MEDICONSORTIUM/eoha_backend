import { Router } from "express";

import { success } from "../lib/apiResponse.js";
import { optionalInt, optionalIsoDate, requireInt } from "../lib/params.js";
import {
  getObservationById,
  searchObservations,
} from "../services/malariaObservationService.js";

const router = Router();

/**
 * List observations. All filters optional.
 * `GET /api/v1/malaria-observations?zoneId=1&date=2026-03-01&page=0&size=20`
 */
router.get("/", (req, res) => {
  const zoneId = optionalInt(req.query.zoneId, "zoneId", null);
  const date = optionalIsoDate(req.query.date, "date");
  const page = optionalInt(req.query.page, "page", 0);
  const size = optionalInt(req.query.size, "size", 20);
  res.json(success(searchObservations(zoneId, date, page, size)));
});

router.get("/:id", (req, res) => {
  const id = requireInt(req.params.id, "id");
  res.json(success(getObservationById(id)));
});

export default router;
