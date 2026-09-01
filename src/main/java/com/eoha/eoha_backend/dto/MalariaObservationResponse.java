package com.eoha.eoha_backend.dto;

import java.time.LocalDate;

/** Shape of an observation returned by {@code /api/v1/malaria-observations}. */
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
}
