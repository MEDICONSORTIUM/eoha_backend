package com.eoha.eoha_backend.dto;

/** Shape of a zone returned by {@code /api/v1/zones}. */
public record ZoneResponse(
        Integer zoneId,
        Integer parentZoneId,
        String zoneName,
        String boundaryPolygon,
        Integer basePopulation,
        String dominantHabitatCode,
        String externalZoneId,
        String zoneType,
        Float latitude,
        Float longitude,
        Float populationDensityPerKm2,
        String municipalityName,
        String provinceName
) {
}
