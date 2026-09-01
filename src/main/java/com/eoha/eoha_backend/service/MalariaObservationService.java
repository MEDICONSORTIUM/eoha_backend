package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.dto.MalariaObservationResponse;
import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Serves canned malaria-observation data so the frontend can see the response shape.
 * Swap the {@link #OBSERVATIONS} list for a real data source later.
 */
@Service
public class MalariaObservationService {

    private static final List<MalariaObservationResponse> OBSERVATIONS = List.of(
            new MalariaObservationResponse(1, 1, LocalDate.parse("2026-03-01"),
                    21.83502f, 0.3396694f, 0.5959259f, -0.38863385f, 17.569027f, 0.021417777f),
            new MalariaObservationResponse(2, 1, LocalDate.parse("2026-03-08"),
                    22.4f, 0.30f, 0.61f, -0.36f, 18.1f, 0.02f),
            new MalariaObservationResponse(3, 2, LocalDate.parse("2026-03-01"),
                    24.9f, 0.21f, 0.48f, -0.44f, 20.2f, 0.05f),
            new MalariaObservationResponse(4, 3, LocalDate.parse("2026-03-01"),
                    28.11f, 0.12f, 0.34f, -0.50f, 24.9f, 0.44f)
    );

    public PagedResponse<MalariaObservationResponse> searchObservations(Integer zoneId, LocalDate date, int page, int size) {
        List<MalariaObservationResponse> filtered = OBSERVATIONS.stream()
                .filter(o -> zoneId == null || zoneId.equals(o.zoneId()))
                .filter(o -> date == null || date.equals(o.dateRecorded()))
                .toList();
        return PagedResponse.of(filtered, page, size);
    }

    public MalariaObservationResponse getObservationById(Integer id) {
        return OBSERVATIONS.stream()
                .filter(o -> o.envRecordId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Malaria observation " + id + " not found"));
    }
}
