package com.eoha.eoha_backend.dto;

import com.eoha.eoha_backend.model.MalariaObservation;

import java.time.LocalDate;

/**
 * API contract for a malaria environmental observation. Decoupled from the
 * {@link MalariaObservation} JPA entity so the persistence model and the API contract
 * can evolve independently.
 */
public record MalariaObservationResponse(
        Integer envRecordId,
        Integer zoneId,
        LocalDate dateRecorded,
        Float lstSurfaceTempC,
        Float soilMoisturePct,
        Float ndviVegetationIndex,
        Float ndwiWaterIndex,
        Float airTempC,
        Float agricPercentage
) {

    public static MalariaObservationResponse from(MalariaObservation observation) {
        return new MalariaObservationResponse(
                observation.getEnvRecordId(),
                observation.getZoneId(),
                observation.getDateRecorded(),
                observation.getLstSurfaceTempC(),
                observation.getSoilMoisturePct(),
                observation.getNdviVegetationIndex(),
                observation.getNdwiWaterIndex(),
                observation.getAirTempC(),
                observation.getAgricPercentage()
        );
    }
}
