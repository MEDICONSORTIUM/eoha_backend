package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.dto.MalariaObservationResponse;
import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.exception.ResourceNotFoundException;
import com.eoha.eoha_backend.repository.MalariaObservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MalariaObservationService {

    private static final Logger log = LoggerFactory.getLogger(MalariaObservationService.class);

    private final MalariaObservationRepository malariaObservationRepository;

    public MalariaObservationService(MalariaObservationRepository malariaObservationRepository) {
        this.malariaObservationRepository = malariaObservationRepository;
    }

    /** Filtered, paginated observation lookup. {@code null} filters are ignored. */
    public PagedResponse<MalariaObservationResponse> searchObservations(Integer zoneId, LocalDate date, Pageable pageable) {
        return PagedResponse.of(
                malariaObservationRepository.search(zoneId, date, pageable),
                MalariaObservationResponse::from
        );
    }

    /** Unpaged variant used by the deprecated path-variable endpoints. */
    public List<MalariaObservationResponse> searchObservations(Integer zoneId, LocalDate date) {
        return malariaObservationRepository.search(zoneId, date, Pageable.unpaged())
                .map(MalariaObservationResponse::from)
                .getContent();
    }

    public MalariaObservationResponse getObservationById(Integer id) {
        return malariaObservationRepository.findById(id)
                .map(MalariaObservationResponse::from)
                .orElseThrow(() -> {
                    log.warn("Malaria observation {} not found", id);
                    return new ResourceNotFoundException("Malaria observation " + id + " not found");
                });
    }
}
