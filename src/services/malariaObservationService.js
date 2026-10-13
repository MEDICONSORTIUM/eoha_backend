import { paginate } from "../lib/paged.js";
import { ResourceNotFoundException } from "../lib/errors.js";

/**
 * Serves canned malaria-observation data so the frontend can see the response
 * shape. Swap the `OBSERVATIONS` array for a real data source later.
 */

const OBSERVATIONS = [
  {
    envRecordId: 1,
    zoneId: 1,
    dateRecorded: "2026-03-01",
    lstSurfaceTempC: 21.83502,
    soilMoisturePct: 0.3396694,
    ndviVegetationIndex: 0.5959259,
    ndwiWaterIndex: -0.38863385,
    airTempC: 17.569027,
    agricPercentage: 0.021417777,
  },
  {
    envRecordId: 2,
    zoneId: 1,
    dateRecorded: "2026-03-08",
    lstSurfaceTempC: 22.4,
    soilMoisturePct: 0.3,
    ndviVegetationIndex: 0.61,
    ndwiWaterIndex: -0.36,
    airTempC: 18.1,
    agricPercentage: 0.02,
  },
  {
    envRecordId: 3,
    zoneId: 2,
    dateRecorded: "2026-03-01",
    lstSurfaceTempC: 24.9,
    soilMoisturePct: 0.21,
    ndviVegetationIndex: 0.48,
    ndwiWaterIndex: -0.44,
    airTempC: 20.2,
    agricPercentage: 0.05,
  },
  {
    envRecordId: 4,
    zoneId: 3,
    dateRecorded: "2026-03-01",
    lstSurfaceTempC: 28.11,
    soilMoisturePct: 0.12,
    ndviVegetationIndex: 0.34,
    ndwiWaterIndex: -0.5,
    airTempC: 24.9,
    agricPercentage: 0.44,
  },
];

export function searchObservations(zoneId, date, page, size) {
  const filtered = OBSERVATIONS.filter(
    (o) =>
      (zoneId == null || zoneId === o.zoneId) &&
      (date == null || date === o.dateRecorded),
  );
  return paginate(filtered, page, size);
}

export function getObservationById(id) {
  const observation = OBSERVATIONS.find((o) => o.envRecordId === id);
  if (!observation) {
    throw new ResourceNotFoundException(`Malaria observation ${id} not found`);
  }
  return observation;
}
