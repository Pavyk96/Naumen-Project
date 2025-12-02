package naumen.java.project.controller;

import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsRequest;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.service.ContractorAnalyticsService;
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
@RequestMapping("/analytics")
public class AnalyticsController {

    private final ContractorAnalyticsService contractorAnalyticsService;

    public AnalyticsController(ContractorAnalyticsService contractorAnalyticsService) {
        this.contractorAnalyticsService = contractorAnalyticsService;
    }

    /**
     * Аналитика по контрагентам
     */
    @Transactional
    @PostMapping("/contractor")
    public ContractorAnalyticsResponse analyzeContractors(
            @RequestBody ContractorAnalyticsRequest request
    ) {
        return contractorAnalyticsService.analyze(request);
    }
}
