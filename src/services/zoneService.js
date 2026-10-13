import { paginate } from "../lib/paged.js";
import { ResourceNotFoundException } from "../lib/errors.js";

/**
 * Serves canned zone data so the frontend can see the response shape. Swap the
 * `ZONES` array for a real data source later; keep the exported function shapes.
 */

const ZONES = [
  {
    zoneId: 1,
    parentZoneId: null,
    zoneName: "ETH_24",
    boundaryPolygon: null,
    basePopulation: 12,
    dominantHabitatCode: "10.0",
    externalZoneId: "59500024",
    zoneType: null,
    latitude: -29.85264,
    longitude: 30.924128,
    populationDensityPerKm2: 1279.1251,
    municipalityName: "Ethekwini Metropolitan Municipality",
    provinceName: "KwaZulu-Natal",
  },
  {
    zoneId: 2,
    parentZoneId: null,
    zoneName: "ETH_25",
    boundaryPolygon: null,
    basePopulation: 88,
    dominantHabitatCode: "10.0",
    externalZoneId: "59500025",
    zoneType: null,
    latitude: -29.8612,
    longitude: 30.9401,
    populationDensityPerKm2: 950.5,
    municipalityName: "Ethekwini Metropolitan Municipality",
    provinceName: "KwaZulu-Natal",
  },
  {
    zoneId: 3,
    parentZoneId: null,
    zoneName: "POL_07",
    boundaryPolygon: null,
    basePopulation: 340,
    dominantHabitatCode: "20.0",
    externalZoneId: "49300007",
    zoneType: null,
    latitude: -23.9045,
    longitude: 29.4689,
    populationDensityPerKm2: 55.2,
    municipalityName: "Polokwane Local Municipality",
    provinceName: "Limpopo",
  },
];

export function searchZones(province, municipality, page, size) {
  const filtered = ZONES.filter(
    (z) =>
      (province == null || province.toLowerCase() === z.provinceName.toLowerCase()) &&
      (municipality == null || municipality.toLowerCase() === z.municipalityName.toLowerCase()),
  );
  return paginate(filtered, page, size);
}

export function getZoneById(id) {
  const zone = ZONES.find((z) => z.zoneId === id);
  if (!zone) throw new ResourceNotFoundException(`Zone ${id} not found`);
  return zone;
}
