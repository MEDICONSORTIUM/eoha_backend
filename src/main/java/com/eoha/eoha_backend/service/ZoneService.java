package com.eoha.eoha_backend.service;

import com.eoha.eoha_backend.dto.PagedResponse;
import com.eoha.eoha_backend.dto.ZoneResponse;
import com.eoha.eoha_backend.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Serves canned zone data so the frontend can see the response shape. Swap the
 * {@link #ZONES} list for a real data source later.
 */
@Service
public class ZoneService {

    private static final List<ZoneResponse> ZONES = List.of(
            new ZoneResponse(1, null, "ETH_24", null, 12, "10.0", "59500024", null,
                    -29.85264f, 30.924128f, 1279.1251f,
                    "Ethekwini Metropolitan Municipality", "KwaZulu-Natal"),
            new ZoneResponse(2, null, "ETH_25", null, 88, "10.0", "59500025", null,
                    -29.8612f, 30.9401f, 950.5f,
                    "Ethekwini Metropolitan Municipality", "KwaZulu-Natal"),
            new ZoneResponse(3, null, "POL_07", null, 340, "20.0", "49300007", null,
                    -23.9045f, 29.4689f, 55.2f,
                    "Polokwane Local Municipality", "Limpopo")
    );

    public PagedResponse<ZoneResponse> searchZones(String province, String municipality, int page, int size) {
        List<ZoneResponse> filtered = ZONES.stream()
                .filter(z -> province == null || province.equalsIgnoreCase(z.provinceName()))
                .filter(z -> municipality == null || municipality.equalsIgnoreCase(z.municipalityName()))
                .toList();
        return PagedResponse.of(filtered, page, size);
    }

    public ZoneResponse getZoneById(Integer id) {
        return ZONES.stream()
                .filter(z -> z.zoneId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Zone " + id + " not found"));
    }
}
