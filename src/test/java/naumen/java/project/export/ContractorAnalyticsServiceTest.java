package naumen.java.project.export;

import naumen.java.project.dto.analytics.contractor.request.ContractorAnalyticsFilters;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsBreakdown;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsResponse;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsSummary;
import naumen.java.project.dto.analytics.contractor.response.ContractorAnalyticsTrends;
import naumen.java.project.model.Contractor;
import naumen.java.project.service.analytics.contractor.ContractorAnalyticsService;
import naumen.java.project.service.analytics.contractor.ContractorBreakdownService;
import naumen.java.project.service.analytics.contractor.ContractorFilterService;
import naumen.java.project.service.analytics.contractor.ContractorSummaryService;
import naumen.java.project.service.analytics.contractor.ContractorTrendsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Assertions;

import java.util.List;

/**
 * Модульные тесты для ContractorAnalyticsService
 *
 * @author Daniil Mezev
 */
@ExtendWith(MockitoExtension.class)
public class ContractorAnalyticsServiceTest {

    private final ContractorFilterService contractorFilterServiceMock;
    private final ContractorSummaryService contractorSummaryServiceMock;
    private final ContractorBreakdownService contractorBreakdownServiceMock;
    private final ContractorTrendsService contractorTrendsServiceMock;

    private final ContractorAnalyticsService contractorAnalyticsService;

    public ContractorAnalyticsServiceTest(
            @Mock ContractorFilterService contractorFilterServiceMock,
            @Mock ContractorSummaryService contractorSummaryServiceMock,
            @Mock ContractorBreakdownService contractorBreakdownServiceMock,
            @Mock ContractorTrendsService contractorTrendsServiceMock
    ) {
        this.contractorFilterServiceMock = contractorFilterServiceMock;
        this.contractorSummaryServiceMock = contractorSummaryServiceMock;
        this.contractorBreakdownServiceMock = contractorBreakdownServiceMock;
        this.contractorTrendsServiceMock = contractorTrendsServiceMock;

        this.contractorAnalyticsService = new ContractorAnalyticsService(
                contractorFilterServiceMock,
                contractorSummaryServiceMock,
                contractorBreakdownServiceMock,
                contractorTrendsServiceMock
        );
    }

    /**
     * Тест на корректность и последовательность вызовов сервисов аналитики, включая тренды
     */
    @Test
    void analyze_includeTrendsTrue_callsServicesInOrder_withCorrectArgs() {
        ContractorAnalyticsFilters filters = Mockito.mock(ContractorAnalyticsFilters.class);
        List<String> dimensions = List.of("city", "segment");
        List<String> metrics = List.of("count", "revenue");

        List<Contractor> contractors = List.of(Mockito.mock(Contractor.class), Mockito.mock(Contractor.class));
        ContractorAnalyticsSummary summary = Mockito.mock(ContractorAnalyticsSummary.class);
        List<ContractorAnalyticsBreakdown> breakdowns = List.of(Mockito.mock(ContractorAnalyticsBreakdown.class));
        ContractorAnalyticsTrends trends = Mockito.mock(ContractorAnalyticsTrends.class);

        Mockito.when(contractorFilterServiceMock.findContractors(filters)).thenReturn(contractors);
        Mockito.when(contractorSummaryServiceMock.buildSummary(contractors)).thenReturn(summary);
        Mockito.when(contractorBreakdownServiceMock.buildBreakdowns(contractors, dimensions, metrics)).thenReturn(breakdowns);
        Mockito.when(contractorTrendsServiceMock.buildTrends(contractors)).thenReturn(trends);

        ContractorAnalyticsResponse response =
                contractorAnalyticsService.analyze(filters, dimensions, metrics, true);

        InOrder inOrder = Mockito.inOrder(
                contractorFilterServiceMock,
                contractorSummaryServiceMock,
                contractorBreakdownServiceMock,
                contractorTrendsServiceMock
        );

        inOrder.verify(contractorFilterServiceMock).findContractors(filters);
        inOrder.verify(contractorSummaryServiceMock).buildSummary(contractors);
        inOrder.verify(contractorBreakdownServiceMock).buildBreakdowns(contractors, dimensions, metrics);
        inOrder.verify(contractorTrendsServiceMock).buildTrends(contractors);
        inOrder.verifyNoMoreInteractions();

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.summary());
        Assertions.assertNotNull(response.breakdown());
        Assertions.assertNotNull(response.trends());
    }

    /**
     * Проверка, что при выключенных трендах, сервис трендов не вызывается
     */
    @Test
    void analyzeIncludeTrendsFalseDoesNotCallTrendsService() {
        ContractorAnalyticsFilters filters = Mockito.mock(ContractorAnalyticsFilters.class);
        List<String> dimensions = List.of("city", "segment");
        List<String> metrics = List.of("count", "revenue");

        List<Contractor> contractors = List.of(Mockito.mock(Contractor.class));
        ContractorAnalyticsSummary summary = Mockito.mock(ContractorAnalyticsSummary.class);
        List<ContractorAnalyticsBreakdown> breakdowns = List.of(Mockito.mock(ContractorAnalyticsBreakdown.class));

        Mockito.when(contractorFilterServiceMock.findContractors(filters)).thenReturn(contractors);
        Mockito.when(contractorSummaryServiceMock.buildSummary(contractors)).thenReturn(summary);
        Mockito.when(contractorBreakdownServiceMock.buildBreakdowns(contractors, dimensions, metrics)).thenReturn(breakdowns);

        ContractorAnalyticsResponse response =
                contractorAnalyticsService.analyze(filters, dimensions, metrics, false);

        InOrder inOrder = Mockito.inOrder(
                contractorFilterServiceMock,
                contractorSummaryServiceMock,
                contractorBreakdownServiceMock
        );

        inOrder.verify(contractorFilterServiceMock).findContractors(filters);
        inOrder.verify(contractorSummaryServiceMock).buildSummary(contractors);
        inOrder.verify(contractorBreakdownServiceMock).buildBreakdowns(contractors, dimensions, metrics);
        inOrder.verifyNoMoreInteractions();

        Mockito.verify(contractorTrendsServiceMock, Mockito.never()).buildTrends(Mockito.anyList());

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.summary());
        Assertions.assertNotNull(response.breakdown());
        Assertions.assertNull(response.trends());
    }
}
