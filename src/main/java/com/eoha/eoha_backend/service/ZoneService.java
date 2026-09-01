package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.dto.ZoneResponse;
import com.eoha.eoha_backend.exception.ResourceNotFoundException;
import com.eoha.eoha_backend.repository.ZoneRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ZoneService {

    private static final Logger log = LoggerFactory.getLogger(ZoneService.class);

    private final ZoneRepository zoneRepository;

    public ZoneService(ZoneRepository zoneRepository) {
        this.zoneRepository = zoneRepository;
    }

    /** Filtered, paginated zone lookup. {@code null} filters are ignored. */
    public PagedResponse<ZoneResponse> searchZones(String province, String municipality, Pageable pageable) {
        return PagedResponse.of(
                zoneRepository.search(province, municipality, pageable),
                ZoneResponse::from
        );
    }

    /** Unpaged variant used by the deprecated path-variable endpoints. */
    public List<ZoneResponse> searchZones(String province, String municipality) {
        return zoneRepository.search(province, municipality, Pageable.unpaged())
                .map(ZoneResponse::from)
                .getContent();
    }

    public ZoneResponse getZoneById(Integer id) {
        return zoneRepository.findById(id)
                .map(ZoneResponse::from)
                .orElseThrow(() -> {
                    log.warn("Zone {} not found", id);
                    return new ResourceNotFoundException("Zone " + id + " not found");
                });
    }
}
