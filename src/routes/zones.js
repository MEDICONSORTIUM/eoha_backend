import { Router } from "express";

import { success } from "../lib/apiResponse.js";
import { optionalInt, optionalString, requireInt } from "../lib/params.js";
import { getZoneById, searchZones } from "../services/zoneService.js";

const router = Router();

/**
 * List zones. All filters optional.
 * `GET /api/v1/zones?province=KwaZulu-Natal&municipality=Ethekwini&page=0&size=20`
 */
router.get("/", (req, res) => {
  const province = optionalString(req.query.province);
  const municipality = optionalString(req.query.municipality);
  const page = optionalInt(req.query.page, "page", 0);
  const size = optionalInt(req.query.size, "size", 20);
  res.json(success(searchZones(province, municipality, page, size)));
});

router.get("/:id", (req, res) => {
  const id = requireInt(req.params.id, "id");
  res.json(success(getZoneById(id)));
});

export default router;
