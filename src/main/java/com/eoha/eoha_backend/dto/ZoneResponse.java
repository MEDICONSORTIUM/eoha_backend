package com.eoha.eoha_backend.dto;

import com.eoha.eoha_backend.model.Zone;

/**
 * API contract for a zone. Decoupled from the {@link Zone} JPA entity so that database
 * schema changes do not directly ripple out to API consumers.
 */
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

    public static ZoneResponse from(Zone zone) {
        return new ZoneResponse(
                zone.getZoneId(),
                zone.getParentZoneId(),
                zone.getZoneName(),
                zone.getBoundaryPolygon(),
                zone.getBasePopulation(),
                zone.getDominantHabitatCode(),
                zone.getExternalZoneId(),
                zone.getZoneType(),
                zone.getLatitude(),
                zone.getLongitude(),
                zone.getPopulationDensityPerKm2(),
                zone.getMunicipalityName(),
                zone.getProvinceName()
        );
    }
}
