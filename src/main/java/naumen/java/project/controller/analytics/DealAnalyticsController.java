package naumen.java.project.controller.analytics;

import jakarta.validation.Valid;
import naumen.java.project.dto.analytics.deal.request.DealAnalyticsRequestDTO;
import naumen.java.project.dto.analytics.deal.response.DealAnalyticsResponseDTO;
import naumen.java.project.service.analytics.deal.DealAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Аналитика по контрагентам и сделкам
 *
 * @author Daniil Mezev
 */
@RestController
@RequestMapping("/analytics/deal")
public class DealAnalyticsController {

    private final DealAnalyticsService dealAnalyticsService;

    public DealAnalyticsController(DealAnalyticsService dealAnalyticsService) {
        this.dealAnalyticsService = dealAnalyticsService;
    }

    /**
     * Аналитика по сделкам
     */
    @Transactional
    @PostMapping
    public ResponseEntity<DealAnalyticsResponseDTO> analyzeDeals(
            @Valid @RequestBody DealAnalyticsRequestDTO request
    ) {
        DealAnalyticsResponseDTO dealAnalyticsResponseDTO = dealAnalyticsService.analyze(
                request.filters(),
                request.dimensions(),
                request.metrics(),
                request.includeFunnel()
        );
        return ResponseEntity.ok(dealAnalyticsResponseDTO);
    }
}
